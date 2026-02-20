package io.devfactory.comment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@NoArgsConstructor(access = PROTECTED)
@Getter
@Table(name = "tb_article_comment_count")
@Entity
public class ArticleCommentCount {

  @Id
  private Long articleId; // shard key
  private Long commentCount;

  public static ArticleCommentCount init(Long articleId, Long commentCount) {
    final var articleCommentCount = new ArticleCommentCount();
    articleCommentCount.articleId = articleId;
    articleCommentCount.commentCount = commentCount;
    return articleCommentCount;
  }

}
