package io.devfactory.like.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PROTECTED;

@NoArgsConstructor(access = PROTECTED)
@Getter
@Table(name = "tb_article_like")
@Entity
public class ArticleLike {

  @Id
  private Long articleLikeId;
  private Long articleId; // shard key
  private Long userId;
  private LocalDateTime createdAt;

  public static ArticleLike create(Long articleLikeId, Long articleId, Long userId) {
    final var articleLike = new ArticleLike();
    articleLike.articleLikeId = articleLikeId;
    articleLike.articleId = articleId;
    articleLike.userId = userId;
    articleLike.createdAt = LocalDateTime.now();
    return articleLike;
  }

}
