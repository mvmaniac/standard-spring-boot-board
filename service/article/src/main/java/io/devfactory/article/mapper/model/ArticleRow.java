package io.devfactory.article.mapper.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.ibatis.type.Alias;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Alias("ArticleRow")
public class ArticleRow {

  private Long articleId;
  private String title;
  private String content;
  private Long boardId;
  private Long writerId;
  private LocalDateTime createdAt;
  private LocalDateTime modifiedAt;

}
