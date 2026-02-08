package io.devfactory.like.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@NoArgsConstructor(access = PROTECTED)
@Getter
@Table(name = "tb_article_like_count")
@Entity
public class ArticleLikeCount {

  @Id
  private Long articleId; // shard key
  private Long likeCount;

//  @Version
  private Long version;

  public static ArticleLikeCount init(Long articleId, Long likeCount) {
    final var articleLikeCount = new ArticleLikeCount();
    articleLikeCount.articleId = articleId;
    articleLikeCount.likeCount = likeCount;
    articleLikeCount.version = 0L;
    return articleLikeCount;
  }

  public void increase() {
    this.likeCount++;
  }

  public void decrease() {
    this.likeCount--;
  }

}
