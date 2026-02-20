package io.devfactory.comment.dto.response;

import io.devfactory.comment.entity.Comment;
import io.devfactory.comment.entity.CommentV2;
import io.devfactory.comment.mapper.model.CommentRow;
import io.devfactory.comment.mapper.model.CommentRowV2;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@Getter
public class CommentResponse {

  private Long commentId;
  private String content;
  private Long parentCommentId;
  private Long articleId;
  private Long writerId;
  private Boolean deleted;
  private String parentPath;
  private String path;
  private LocalDateTime createdAt;

  public static CommentResponse from(Comment comment) {
    final var response = new CommentResponse();
    response.commentId = comment.getCommentId();
    response.content = comment.getContent();
    response.parentCommentId = comment.getParentCommentId();
    response.articleId = comment.getArticleId();
    response.writerId = comment.getWriterId();
    response.deleted = comment.getDeleted();
    response.createdAt = comment.getCreatedAt();
    return response;
  }

  public static CommentResponse from(CommentRow row) {
    final var response = new CommentResponse();
    response.commentId = row.getCommentId();
    response.content = row.getContent();
    response.parentCommentId = row.getParentCommentId();
    response.articleId = row.getArticleId();
    response.writerId = row.getWriterId();
    response.deleted = row.getDeleted();
    response.createdAt = row.getCreatedAt();
    return response;
  }

  public static CommentResponse from(CommentV2 comment) {
    final var response = new CommentResponse();
    response.commentId = comment.getCommentId();
    response.content = comment.getContent();
    response.articleId = comment.getArticleId();
    response.writerId = comment.getWriterId();
    response.deleted = comment.getDeleted();
    response.createdAt = comment.getCreatedAt();

    final var commentPath = comment.getCommentPath();
    response.parentPath = commentPath.getParentPath();
    response.path = commentPath.getPath();
    return response;
  }

  public static CommentResponse from(CommentRowV2 row) {
    final var response = new CommentResponse();
    response.commentId = row.getCommentId();
    response.content = row.getContent();
    response.articleId = row.getArticleId();
    response.writerId = row.getWriterId();
    response.deleted = row.getDeleted();
    response.createdAt = row.getCreatedAt();

    final var commentPath = row.getCommentPath();
    response.parentPath = commentPath.getParentPath();
    response.path = commentPath.getPath();
    return response;
  }

}
