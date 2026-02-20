package io.devfactory.comment.dto.request;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class CommentUpdateRequest {

  private String content;

  public static CommentUpdateRequest of(String content) {
    final var request = new CommentUpdateRequest();
    request.content = content;
    return request;
  }

}
