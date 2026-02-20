package io.devfactory.comment.mapper.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.ibatis.type.Alias;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Alias("CommentRow")
public class CommentRow {

  private Long commentId;
  private String content;
  private Long parentCommentId;
  private Long articleId;
  private Long writerId;
  private Boolean deleted;
  private LocalDateTime createdAt;

}
