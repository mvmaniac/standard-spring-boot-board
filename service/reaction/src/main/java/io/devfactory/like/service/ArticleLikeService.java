package io.devfactory.like.service;

import io.devfactory.common.Snowflake;
import io.devfactory.like.dto.response.ArticleLikeResponse;
import io.devfactory.like.entity.ArticleLike;
import io.devfactory.like.entity.ArticleLikeCount;
import io.devfactory.like.mapper.ArticleLikeCountMapper;
import io.devfactory.like.repository.ArticleLikeCountRepository;
import io.devfactory.like.repository.ArticleLikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ArticleLikeService {

  private final ArticleLikeRepository articleLikeRepository;
  private final ArticleLikeCountRepository articleLikeCountRepository;
  private final ArticleLikeCountMapper articleLikeCountMapper;

  private final Snowflake snowflake = new Snowflake();

  public Long count(Long articleId) {
    return articleLikeCountRepository.findById(articleId)
        .map(ArticleLikeCount::getLikeCount)
        .orElse(0L);
  }

  public ArticleLikeResponse read(Long articleId, Long userId) {
    return articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
        .map(ArticleLikeResponse::from)
        .orElseThrow();
  }

  // update문을 통한 Lock
  @Transactional
  public void likeWithPessimisticLock1(Long articleId, Long userId) {
    final var newArticleLike = ArticleLike.create(snowflake.nextId(), articleId, userId);
    articleLikeRepository.save(newArticleLike);
    articleLikeRepository.flush(); // 명시적 호출

    // 최초 요청 시에는 update 되는 레코드가 없으므로 1로 초기화
    // 그 이후에는 업데이트가 되므로 이를 위해 upsert 사용, 원래는 생성시 같이 0으로 생성하는게 권장됨...
    articleLikeCountMapper.upsertLikeCount(articleId);
  }

  @Transactional
  public void unlikeWithPessimisticLock1(Long articleId, Long userId) {
    final var articleLike = articleLikeRepository.findByArticleIdAndUserId(articleId, userId).orElse(null);
    if (articleLike == null) return;

    articleLikeRepository.delete(articleLike);
    articleLikeRepository.flush(); // 명시적 호출

    articleLikeCountMapper.decreaseLikeCount(articleId);
  }

  // select ... for update + update
  @Transactional
  public void likeWithPessimisticLock2(Long articleId, Long userId) {
    final var newArticleLike = ArticleLike.create(snowflake.nextId(), articleId, userId);
    articleLikeRepository.save(newArticleLike);

    // findLockedByArticleId에서 lock이 안잡히고 orElseGet으로 빠지는 경우에
    // 여러 스레드가 동시에 들어온 경우라면 경쟁상태가 될 수 있음
    // 생성 시점에 init을 해주는게 제일 나은듯...
    final var articleLikeCount = articleLikeCountRepository.findLockedByArticleId(articleId)
        .orElseGet(() -> ArticleLikeCount.init(articleId, 0L));

    articleLikeCount.increase();
    articleLikeCountRepository.save(articleLikeCount); // 최초 없을 수 있으므로 명시적으로 호출
  }

  @Transactional
  public void unlikeWithPessimisticLock2(Long articleId, Long userId) {
    final var articleLike = articleLikeRepository.findByArticleIdAndUserId(articleId, userId).orElse(null);
    if (articleLike == null) return;

    articleLikeRepository.delete(articleLike);

    final var articleLikeCount = articleLikeCountRepository.findLockedByArticleId(articleId).orElseThrow();
    articleLikeCount.decrease();
  }

  @Transactional
  public void likeWithOptimisticLock(Long articleId, Long userId) {
    final var newArticleLike = ArticleLike.create(snowflake.nextId(), articleId, userId);
    articleLikeRepository.save(newArticleLike);

    final var articleLikeCount = articleLikeCountRepository.findById(articleId)
        .orElseGet(() -> ArticleLikeCount.init(articleId, 0L));

    articleLikeCount.increase();
    articleLikeCountRepository.save(articleLikeCount); // 최초 없을 수 있으므로 명시적으로 호출
  }

  @Transactional
  public void unlikeWithOptimisticLock(Long articleId, Long userId) {
    final var articleLike = articleLikeRepository.findByArticleIdAndUserId(articleId, userId).orElse(null);
    if (articleLike == null) return;

    articleLikeRepository.delete(articleLike);

    final var articleLikeCount = articleLikeCountRepository.findById(articleId).orElseThrow();
    articleLikeCount.decrease();
  }

}
