package io.devfactory.comment.dto.request;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class CommentCreateRequest {

  private Long articleId;
  private String content;
  private Long parentCommentId;
  private Long writerId;

  public static CommentCreateRequest of(
    Long articleId,
    String content,
    Long parentCommentId,
    Long writerId
  ) {
    final var request = new CommentCreateRequest();
    request.articleId = articleId;
    request.content = content;
    request.parentCommentId = parentCommentId;
    request.writerId = writerId;
    return request;
  }

}
