package io.devfactory.view.service;

import io.devfactory.view.repository.ArticleViewCountRepository;
import io.devfactory.view.repository.ArticleViewDistributedLockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class ArticleViewService {

  private static final int BACK_UP_BACH_SIZE = 100;
  private static final Duration TTL = Duration.ofMinutes(10);

  private final ArticleViewCountRepository articleViewCountRepository;
  private final ArticleViewDistributedLockRepository articleViewDistributedLockRepository;

  private final ArticleViewCountBackUpProcessor articleViewCountBackUpProcessor;

  public Long increase(Long articleId, Long userId) {
    if (!articleViewDistributedLockRepository.lock(articleId, userId, TTL)) {
      return articleViewCountRepository.read(articleId);
    }

    // count % 100 == 0일 때만 DB에 백업하는데,
    // 만약 조회수가 199에서 멈춘 상태로 Redis가 장애가 나거나 재기동되면 101~199 사이의 조회수는 DB에 영영 기록되지 않을 수 있음
    // 중요한 데이터라면 별도의 스케줄러가 주기적으로 Redis의 모든 조회수를 DB로 덮어쓰는(Sync) 로직을 병행하는 것이 안전
    final var count = articleViewCountRepository.increase(articleId);
    if (count % BACK_UP_BACH_SIZE == 0) {
      articleViewCountBackUpProcessor.backUp(articleId, count);
    }

    return count;
  }

  public Long count(Long articleId) {
    return articleViewCountRepository.read(articleId);
  }

}
