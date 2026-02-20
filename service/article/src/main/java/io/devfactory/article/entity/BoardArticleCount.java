package io.devfactory.article.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@NoArgsConstructor(access = PROTECTED)
@Getter
@Table(name = "tb_board_article_count")
@Entity
public class BoardArticleCount {

  @Id
  private Long boardId; // shard key
  private Long articleCount;

  public static BoardArticleCount init(Long boardId, Long articleCount) {
    final var boardArticleCount = new BoardArticleCount();
    boardArticleCount.boardId = boardId;
    boardArticleCount.articleCount = articleCount;
    return boardArticleCount;
  }

}
