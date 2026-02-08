package io.devfactory.comment.dto.request;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class CommentCreateRequestV2 {

  private Long articleId;
  private String content;
  private String parentPath;
  private Long writerId;

  public static CommentCreateRequestV2 of(Long articleId, String content, String parentPath,
      Long writerId) {
    final var request = new CommentCreateRequestV2();
    request.articleId = articleId;
    request.content = content;
    request.parentPath = parentPath;
    request.writerId = writerId;
    return request;
  }

}
