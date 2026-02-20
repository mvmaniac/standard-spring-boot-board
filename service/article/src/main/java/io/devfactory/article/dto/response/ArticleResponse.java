package io.devfactory.article.dto.response;

import io.devfactory.article.entity.Article;
import io.devfactory.article.mapper.model.ArticleRow;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@Getter
public class ArticleResponse {

  private Long articleId;
  private String title;
  private String content;
  private Long boardId;
  private Long writerId;
  private LocalDateTime createdAt;
  private LocalDateTime modifiedAt;

  public static ArticleResponse from(Article article) {
    final var response = new ArticleResponse();
    response.articleId = article.getArticleId();
    response.title = article.getTitle();
    response.content = article.getContent();
    response.boardId = article.getBoardId();
    response.writerId = article.getWriterId();
    response.createdAt = article.getCreatedAt();
    response.modifiedAt = article.getModifiedAt();
    return response;
  }

  public static ArticleResponse from(ArticleRow articleRow) {
    final var response = new ArticleResponse();
    response.articleId = articleRow.getArticleId();
    response.title = articleRow.getTitle();
    response.content = articleRow.getContent();
    response.boardId = articleRow.getBoardId();
    response.writerId = articleRow.getWriterId();
    response.createdAt = articleRow.getCreatedAt();
    response.modifiedAt = articleRow.getModifiedAt();
    return response;
  }

}
