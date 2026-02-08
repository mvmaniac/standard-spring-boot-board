package io.devfactory.comment.mapper;

import io.devfactory.comment.config.MyBatisConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@Import(MyBatisConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@MybatisTest
class CommentMapperTest {

  @Autowired
  private CommentMapper commentMapper;

  @DisplayName("countCommentsWithLimitTest")
  @ParameterizedTest
  @MethodSource("countParams")
  void countCommentChildrenTest(Long articleId, Long limit) {
    final var count = commentMapper.countCommentsWithLimit(articleId, limit);
    assertThat(count).isGreaterThanOrEqualTo(0L);
  }

  @DisplayName("findCommentsWithPagingTest")
  @ParameterizedTest
  @MethodSource("pagingParams")
  void findCommentsWithPagingTest(Long articleId, Long offset, Long limit) {
    final var rows = commentMapper.findCommentsWithPaging(articleId, offset, limit);
    assertThat(rows).hasSizeLessThanOrEqualTo(limit.intValue());
  }

  @DisplayName("findCommentsWithScrollTest")
  @ParameterizedTest
  @MethodSource("scrollParams")
  void findCommentsWithScrollTest(Long articleId, Long limit) {
    final var comments = commentMapper.findCommentsWithScroll(articleId, limit);

    assertThat(comments)
        .isNotNull()
        .hasSizeLessThanOrEqualTo(limit.intValue());

    if (comments.isEmpty()) return;

    final var lastParentCommentId = comments.getLast().getParentCommentId();
    final var lastCommentId = comments.getLast().getCommentId();
    final var nextComments = commentMapper.findCommentsNextWithScroll(articleId, limit, lastParentCommentId, lastCommentId);

    assertThat(nextComments)
        .isNotNull()
        .hasSizeLessThanOrEqualTo(limit.intValue())
        .allSatisfy(comment -> assertThat(comment.getParentCommentId()).isGreaterThanOrEqualTo(lastParentCommentId));
  }

  private static Stream<Arguments> pagingParams() {
    return Stream.of(
        Arguments.of(1L, 0L, 30L)
    );
  }

  private static Stream<Arguments> countParams() {
    return Stream.of(
        Arguments.of(1L, 10000L)
    );
  }

  private static Stream<Arguments> scrollParams() {
    return Stream.of(
        Arguments.of(1L, 30L)
    );
  }

}
