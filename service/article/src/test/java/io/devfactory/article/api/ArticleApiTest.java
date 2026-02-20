package io.devfactory.article.api;

import io.devfactory.article.dto.request.ArticleCreateRequest;
import io.devfactory.article.dto.request.ArticleUpdateRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@ActiveProfiles("test")
@Import(RestClientTestConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ArticleApiTest {

  @Autowired
  private ArticleApiClient api;

  @DisplayName("createTest")
  @ParameterizedTest
  @MethodSource("createRequests")
  void createTest(ArticleCreateRequest request) {
    final var response = api.create(request);

    assertThat(response).isNotNull();
    assertThat(api.delete(response.getArticleId())).isTrue();

    log.info("[dev] createTest response = {}", response);
  }

  @DisplayName("readTest")
  @ParameterizedTest
  @MethodSource("createRequests")
  void readTest(ArticleCreateRequest request) {
    final var created = api.create(request);
    final var response = api.read(created.getArticleId());

    assertThat(response).isNotNull();
    assertThat(response.getArticleId()).isEqualTo(created.getArticleId());
    assertThat(api.delete(created.getArticleId())).isTrue();

    log.info("[dev] readTest response = {}", response);
  }

  @DisplayName("updateTest")
  @ParameterizedTest
  @MethodSource("updateRequests")
  void updateTest(ArticleCreateRequest createRequest, ArticleUpdateRequest updateRequest) {
    final var created = api.create(createRequest);
    final var response = api.update(created.getArticleId(), updateRequest);

    assertThat(response).isNotNull();
    assertThat(response.getTitle()).isEqualTo(updateRequest.getTitle());
    assertThat(response.getContent()).isEqualTo(updateRequest.getContent());
    assertThat(api.delete(created.getArticleId())).isTrue();

    log.info("[dev] updateTest response = {}", response);
  }

  @DisplayName("create -> update -> delete flow")
  @Test
  void createUpdateDeleteFlow() {
    final var created = api.create(ArticleCreateRequest.of("flow-title", "flow-content", 1L, 1L));
    final var updated = api.update(created.getArticleId(),
      ArticleUpdateRequest.of("flow-title-2", "flow-content-2"));

    assertThat(updated.getArticleId()).isEqualTo(created.getArticleId());
    assertThat(api.delete(created.getArticleId())).isTrue();

    log.info("[dev] flow response = {}", updated);
  }

  @DisplayName("readWithPagingTest")
  @ParameterizedTest
  @MethodSource("readWithPagingRequests")
  void readWithPagingTest(Long boardId, Long page, Long pageSize) {
    final var response = api.readWithPaging(boardId, page, pageSize);

    assertThat(response).isNotNull();
    assertThat(response.getArticles()).isNotNull().hasSizeLessThanOrEqualTo(pageSize.intValue());

    log.info("[dev] readAllTest articles = {}", response.getArticles());
  }

  @DisplayName("readWithScrollTest")
  @ParameterizedTest
  @MethodSource("readWithScrollRequests")
  void readWithScrollTest(Long boardId, Long pageSize) {
    final var firstPage = api.readWithScroll(boardId, pageSize);

    assertThat(firstPage).isNotNull().hasSizeLessThanOrEqualTo(pageSize.intValue());

    if (firstPage.isEmpty()) return;

    final var lastArticleId = firstPage.getLast().getArticleId();
    final var nextPage = api.readWithScroll(boardId, pageSize, lastArticleId);

    assertThat(nextPage)
      .isNotNull()
      .hasSizeLessThanOrEqualTo(pageSize.intValue())
      .allSatisfy(article -> assertThat(article.getArticleId()).isLessThan(lastArticleId));

    log.info("[dev] readAllTest nextPage = {}", nextPage);
  }

  private static Stream<ArticleCreateRequest> createRequests() {
    return Stream.of(
      ArticleCreateRequest.of("hi", "my content", 1L, 1L),
      ArticleCreateRequest.of("hello", "my content 2", 2L, 1L)
    );
  }

  private static Stream<Arguments> updateRequests() {
    return Stream.of(
      Arguments.of(
        ArticleCreateRequest.of("hi", "my content", 1L, 1L),
        ArticleUpdateRequest.of("hi 2", "my content 22")
      ),
      Arguments.of(
        ArticleCreateRequest.of("hello", "my content 2", 2L, 1L),
        ArticleUpdateRequest.of("hello 2", "my content 222")
      )
    );
  }

  private static Stream<Arguments> readWithPagingRequests() {
    return Stream.of(
      Arguments.of(1L, 50000L, 30L)
    );
  }

  private static Stream<Arguments> readWithScrollRequests() {
    return Stream.of(
      Arguments.of(1L, 5L)
    );
  }

}
