package io.devfactory.like.dto.response;

import io.devfactory.like.entity.ArticleLike;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@Getter
public class ArticleLikeResponse {

  private Long articleLikeId;
  private Long articleId;
  private Long userId;
  private LocalDateTime createdAt;

  public static ArticleLikeResponse from(ArticleLike articleLike) {
    ArticleLikeResponse response = new ArticleLikeResponse();
    response.articleLikeId = articleLike.getArticleLikeId();
    response.articleId = articleLike.getArticleId();
    response.userId = articleLike.getUserId();
    response.createdAt = articleLike.getCreatedAt();
    return response;
  }

}
