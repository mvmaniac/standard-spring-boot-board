package io.devfactory.article.dto.request;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class ArticleCreateRequest {

  private String title;
  private String content;
  private Long writerId;
  private Long boardId;

  public static ArticleCreateRequest of(String title, String content, Long writerId, Long boardId) {
    final var request = new ArticleCreateRequest();
    request.title = title;
    request.content = content;
    request.writerId = writerId;
    request.boardId = boardId;
    return request;
  }

}
