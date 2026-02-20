package io.devfactory.article.dto.request;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class ArticleUpdateRequest {

  private String title;
  private String content;

  public static ArticleUpdateRequest of(String title, String content) {
    final var request = new ArticleUpdateRequest();
    request.title = title;
    request.content = content;
    return request;
  }

}
