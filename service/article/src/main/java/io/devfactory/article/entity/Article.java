package io.devfactory.article.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PROTECTED;

@NoArgsConstructor(access = PROTECTED)
@Getter
@Table(name = "tb_article")
@Entity
public class Article {

  @Id
  private Long articleId;
  private String title;
  private String content;
  private Long boardId; // shard key
  private Long writerId;
  private LocalDateTime createdAt;
  private LocalDateTime modifiedAt;

  public static Article create(
    Long articleId,
    String title,
    String content,
    Long boardId,
    Long writerId
  ) {
    final var article = new Article();
    article.articleId = articleId;
    article.title = title;
    article.content = content;
    article.boardId = boardId;
    article.writerId = writerId;
    article.createdAt = LocalDateTime.now();
    article.modifiedAt = article.createdAt;
    return article;
  }

  public void update(String title, String content) {
    this.title = title;
    this.content = content;
    this.modifiedAt = LocalDateTime.now();
  }

}
