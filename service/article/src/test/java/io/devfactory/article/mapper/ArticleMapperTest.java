package io.devfactory.article.mapper;

import io.devfactory.article.config.MyBatisConfig;
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
class ArticleMapperTest {

  @Autowired
  private ArticleMapper articleMapper;

  @DisplayName("findArticlesWithPagingTest")
  @ParameterizedTest
  @MethodSource("pagingParams")
  void findArticlesWithPagingTest(Long boardId, Long offset, Long limit) {
    final var rows = articleMapper.findArticlesWithPaging(boardId, offset, limit);
    assertThat(rows).hasSizeLessThanOrEqualTo(limit.intValue());
  }

  @DisplayName("countArticlesWithLimitTest")
  @ParameterizedTest
  @MethodSource("countParams")
  void countArticlesWithLimitTest(Long boardId, Long limit) {
    final var count = articleMapper.countArticlesWithLimit(boardId, limit);
    assertThat(count).isGreaterThanOrEqualTo(0L);
  }

  @DisplayName("findArticlesWithScrollTest")
  @ParameterizedTest
  @MethodSource("scrollParams")
  void findArticlesWithScrollTest(Long boardId, Long limit) {
    final var articles = articleMapper.findArticlesWithScroll(boardId, limit);

    assertThat(articles)
      .isNotNull()
      .hasSizeLessThanOrEqualTo(limit.intValue());

    if (articles.isEmpty()) return;

    final var lastArticleId = articles.getLast().getArticleId();
    final var nextArticles = articleMapper.findArticlesNextWithScroll(boardId, limit, lastArticleId);

    assertThat(nextArticles)
      .isNotNull()
      .hasSizeLessThanOrEqualTo(limit.intValue())
      .allSatisfy(article -> assertThat(article.getArticleId()).isLessThan(lastArticleId));
  }

  private static Stream<Arguments> pagingParams() {
    return Stream.of(
      Arguments.of(1L, 1499970L, 30L)
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
