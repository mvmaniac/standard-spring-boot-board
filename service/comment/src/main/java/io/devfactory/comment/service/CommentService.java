package io.devfactory.comment.service;

import io.devfactory.comment.dto.request.CommentCreateRequest;
import io.devfactory.comment.dto.response.CommentPageResponse;
import io.devfactory.comment.dto.response.CommentResponse;
import io.devfactory.comment.entity.Comment;
import io.devfactory.comment.mapper.CommentMapper;
import io.devfactory.comment.repository.CommentRepository;
import io.devfactory.common.snowflake.Snowflake;
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
public class CommentService {

  private final CommentRepository commentRepository;
  private final CommentMapper commentMapper;

  private final Snowflake snowflake = new Snowflake();

  public CommentPageResponse readWithPaging(Long articleId, Long page, Long pageSize) {
    final var comments = commentMapper.findCommentsWithPaging(articleId, (page - 1) * pageSize, pageSize).stream()
      .map(CommentResponse::from)
      .toList();

    final var commentCount = commentMapper.countCommentsWithLimit(articleId,
      PageLimitCalculator.calculatePageLimit(page, pageSize, 10L));

    return CommentPageResponse.of(comments, commentCount);
  }

  public List<CommentResponse> readWithScroll(
    Long articleId,
    Long pageSize,
    Long lastParentCommentId,
    Long lastCommentId
  ) {
    final var comments = lastParentCommentId == null || lastCommentId == null
      ? commentMapper.findCommentsWithScroll(articleId, pageSize)
      : commentMapper.findCommentsNextWithScroll(articleId, pageSize, lastParentCommentId, lastCommentId);
    return comments.stream().map(CommentResponse::from).toList();
  }

  public CommentResponse read(Long commentId) {
    return CommentResponse.from(commentRepository.findById(commentId).orElseThrow());
  }

  @Transactional
  public CommentResponse create(CommentCreateRequest request) {
    final var parentComment = this.findParentComment(request);
    final var parentCommentId = parentComment == null ? null : parentComment.getCommentId();

    final var newComment = Comment.create(snowflake.nextId(), request.getContent(), parentCommentId, request.getArticleId(), request.getWriterId());
    final var savedComment = commentRepository.save(newComment);
    return CommentResponse.from(savedComment);
  }

  private Comment findParentComment(CommentCreateRequest request) {
    final var parentCommentId = request.getParentCommentId();
    if (parentCommentId == null) return null;

    return commentRepository.findById(parentCommentId)
      .filter(not(Comment::getDeleted))
      .filter(Comment::isRoot)
      .orElseThrow();
  }

  @Transactional
  public void delete(Long commentId) {
    final var comment = commentRepository.findById(commentId)
      .filter(not(Comment::getDeleted))
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

  private boolean hasChildren(Comment comment) {
    return commentMapper.countCommentChildren(comment.getArticleId(), comment.getCommentId(), 2L) == 2;
  }

  private void deleteRecursively(Comment comment) {
    commentRepository.delete(comment);
    commentRepository.flush(); // myBatis 때문에 명시적 호출

    if (comment.isRoot()) return;

    commentRepository.findById(comment.getParentCommentId())
      .filter(Comment::getDeleted)
      .filter(not(this::hasChildren))
      .ifPresent(this::deleteRecursively);
  }

}
