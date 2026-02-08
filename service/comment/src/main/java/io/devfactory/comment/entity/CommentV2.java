package io.devfactory.comment.entity;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PROTECTED;

@NoArgsConstructor(access = PROTECTED)
@Getter
@Table(name = "tb_comment_v2")
@Entity
public class CommentV2 {

  @Id
  private Long commentId;
  private String content;
  private Long articleId; // shard key
  private Long writerId;
  private Boolean deleted;
  private LocalDateTime createdAt;

  @Embedded
  private CommentPath commentPath;

  public static CommentV2 create(Long commentId, String content, Long articleId, Long writerId,
      CommentPath commentPath) {
    CommentV2 comment = new CommentV2();
    comment.commentId = commentId;
    comment.content = content;
    comment.articleId = articleId;
    comment.writerId = writerId;
    comment.commentPath = commentPath;
    comment.deleted = false;
    comment.createdAt = LocalDateTime.now();
    return comment;
  }

  public boolean isRoot() {
    return commentPath.isRoot();
  }

  public void delete() {
    deleted = true;
  }

}
