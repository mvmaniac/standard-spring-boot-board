package io.devfactory.comment.mapper.model;

import io.devfactory.comment.entity.CommentPath;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.ibatis.type.Alias;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Alias("CommentRowV2")
public class CommentRowV2 {

  private Long commentId;
  private String content;
  private Long parentCommentId;
  private Long articleId;
  private Long writerId;
  private Boolean deleted;
  private String path;
  private LocalDateTime createdAt;

  private CommentPath commentPath;

  public void setPath(String path) {
    this.path = path;
    this.commentPath = CommentPath.create(this.path);
  }

}
