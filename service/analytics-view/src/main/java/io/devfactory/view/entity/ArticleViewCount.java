package io.devfactory.view.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@NoArgsConstructor(access = PROTECTED)
@Getter
@Table(name = "tb_article_view_count")
@Entity
public class ArticleViewCount {

  @Id
  private Long articleId; // shard key
  private Long viewCount;

  public static ArticleViewCount init(Long articleId, Long viewCount) {
    final var articleViewCount = new ArticleViewCount();
    articleViewCount.articleId = articleId;
    articleViewCount.viewCount = viewCount;
    return articleViewCount;
  }

}
