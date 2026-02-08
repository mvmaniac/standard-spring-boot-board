package io.devfactory.article.dto.response;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
public class ArticlePageResponse {

  private List<ArticleResponse> articles;
  private Long articleCount;

  public static ArticlePageResponse of(List<ArticleResponse> articles, Long articleCount) {
    final var response = new ArticlePageResponse();
    response.articles = articles;
    response.articleCount = articleCount;
    return response;
  }

}
