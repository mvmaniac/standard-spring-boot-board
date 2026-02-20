package io.devfactory.comment.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CommentPathTest {

  @Test
  void createChildCommentTest() {
    // 최초 생성
    // 00000 <- 생성
    this.createChildCommentTest(CommentPath.create(""), null, "00000");

    // 최초 하위 댓글 생성
    // 00000
    //      00000 <- 생성
    this.createChildCommentTest(CommentPath.create("00000"), null, "0000000000");

    // 동일한 레벨의 댓글 생성
    // 00000
    //      00000 <- 생성
    //      00001 <- 생성
    this.createChildCommentTest(CommentPath.create(""), "00000", "00001");

    // 무한 레벨 댓글 생성
    // 0000z
    //      abcdz
    //            zzzzz
    //                  zzzzz
    //      abce0 <- 생성
    this.createChildCommentTest(CommentPath.create("0000z"), "0000zabcdzzzzzzzzzzz", "0000zabce0");
  }

  void createChildCommentTest(CommentPath commentPath, String descendantsTopPath,
    String expectedChildPath
  ) {
    final var childCommentPath = commentPath.createChildCommentPath(descendantsTopPath);
    assertThat(childCommentPath.getPath()).isEqualTo(expectedChildPath);
  }

  @Test
  void createChildCommentPathIfMaxDepthTest() {
    final var commentPath = CommentPath.create("zzzzz".repeat(5));

    assertThatThrownBy(() -> commentPath.createChildCommentPath(null))
      .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void createChildCommentPathIfChunkOverflowTest() {
    final var commentPath = CommentPath.create("");

    assertThatThrownBy(() -> commentPath.createChildCommentPath("zzzzz"))
      .isInstanceOf(IllegalStateException.class);
  }

}
