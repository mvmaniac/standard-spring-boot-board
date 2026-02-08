package io.devfactory.comment.service;

import io.devfactory.comment.dto.request.CommentCreateRequestV2;
import io.devfactory.comment.dto.response.CommentPageResponse;
import io.devfactory.comment.dto.response.CommentResponse;
import io.devfactory.comment.entity.ArticleCommentCount;
import io.devfactory.comment.entity.CommentPath;
import io.devfactory.comment.entity.CommentV2;
import io.devfactory.comment.mapper.ArticleCommentCountMapper;
import io.devfactory.comment.mapper.CommentMapperV2;
import io.devfactory.comment.repository.ArticleCommentCountRepository;
import io.devfactory.comment.repository.CommentRepositoryV2;
import io.devfactory.common.Snowflake;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.function.Predicate.not;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CommentServiceV2 {

  private final CommentRepositoryV2 commentRepository;
  private final CommentMapperV2 commentMapper;

  private final ArticleCommentCountRepository articleCommentCountRepository;
  private final ArticleCommentCountMapper articleCommentCountMapper;

  private final Snowflake snowflake = new Snowflake();

  public CommentPageResponse readWithPaging(Long articleId, Long page, Long pageSize) {
    final var comments = commentMapper.findCommentsWithPaging(articleId, (page - 1) * pageSize, pageSize).stream()
        .map(CommentResponse::from)
        .toList();
    final var commentCount = commentMapper.countCommentsWithLimit(
        articleId, PageLimitCalculator.calculatePageLimit(page, pageSize, 10L));
    return CommentPageResponse.of(comments, commentCount);
  }

  public List<CommentResponse> readWithScroll(Long articleId, Long pageSize, String lastPath) {
    final var comments = lastPath == null
        ? commentMapper.findCommentsWithScroll(articleId, pageSize)
        : commentMapper.findCommentsNextWithScroll(articleId, pageSize, lastPath);
    return comments.stream().map(CommentResponse::from).toList();
  }

  public CommentResponse read(Long commentId) {
    return CommentResponse.from(commentRepository.findById(commentId).orElseThrow());
  }

  @Transactional
  public CommentResponse create(CommentCreateRequestV2 request) {
    final var parent = this.findParent(request);

    final var parentCommentPath = parent == null ? CommentPath.create("") : parent.getCommentPath();
    final var descendantTopPath = commentMapper.findDescendantsTopPath(request.getArticleId(), parentCommentPath.getPath()).orElse(null);
    final var commentPath = parentCommentPath.createChildCommentPath(descendantTopPath);

    final var newComment = CommentV2.create(snowflake.nextId(), request.getContent(), request.getArticleId(), request.getWriterId(), commentPath);
    final var savedComment = commentRepository.save(newComment);
    commentRepository.flush(); // 명시적 호출

    int result = articleCommentCountMapper.incrementArticleCommentCount(savedComment.getArticleId());
    if (result == 0) {
      articleCommentCountMapper.initArticleCommentCount(savedComment.getArticleId());
    }

    return CommentResponse.from(savedComment);
  }

  private CommentV2 findParent(CommentCreateRequestV2 request) {
    final var parentPath = request.getParentPath();
    if (parentPath == null) return null;

    return commentRepository.findByPath(parentPath)
        .filter(not(CommentV2::getDeleted))
        .orElseThrow();
  }

  @Transactional
  public void delete(Long commentId) {
    final var comment = commentRepository.findById(commentId)
        .filter(not(CommentV2::getDeleted))
        .orElse(null);

    if (comment == null) return;

    // 자식이 있으면 삭제 표시만
    if (this.hasChildren(comment)) {
      comment.delete();
      return;
    }

    // 없으면 삭제 처리
    this.deleteRecursively(comment);
  }

  private boolean hasChildren(CommentV2 comment) {
    return commentMapper.findDescendantsTopPath(comment.getArticleId(), comment.getCommentPath().getPath()).isPresent();
  }

  private void deleteRecursively(CommentV2 comment) {
    commentRepository.delete(comment);
    commentRepository.flush(); // myBatis 때문에 명시적 호출

    articleCommentCountMapper.decreaseArticleCommentCount(comment.getArticleId());

    if (comment.isRoot()) return;

    commentRepository.findByPath(comment.getCommentPath().getParentPath())
        .filter(CommentV2::getDeleted)
        .filter(not(this::hasChildren))
        .ifPresent(this::deleteRecursively);
  }

  public Long countComments(Long articleId) {
    return articleCommentCountRepository.findById(articleId)
        .map(ArticleCommentCount::getCommentCount)
        .orElse(0L);
  }

}
