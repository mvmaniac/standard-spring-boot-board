package io.devfactory.comment.api;

import io.devfactory.comment.dto.request.CommentCreateRequest;
import io.devfactory.comment.dto.request.CommentCreateRequestV2;
import io.devfactory.comment.dto.response.CommentResponse;
import jakarta.persistence.EntityNotFoundException;
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
import org.springframework.web.client.HttpServerErrorException;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@ActiveProfiles("test")
@Import(RestClientTestConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CommentApiTest {

  @Autowired
  private CommentApiClient api;

  @DisplayName("createTest")
  @ParameterizedTest
  @MethodSource("createRequests")
  void createTest(CommentCreateRequest request) {
    final var response = api.create(request);

    assertThat(response).isNotNull();
    assertThat(api.delete(response.getCommentId())).isTrue();

    log.info("[dev] createTest response = {}", response);
  }

  @DisplayName("createWithChildrenTest")
  @ParameterizedTest
  @MethodSource("createRequests")
  void createWithChildrenTest(CommentCreateRequest request) {
    // 첫번째 댓글
    final var response = api.create(request);
    assertThat(response).isNotNull();

    log.info("[dev] createWithChildrenTest response = {}", response);

    // 첫번째 댓글의 첫 번째 댓글
    final var requestChild1 = CommentCreateRequest.of(1L, "my-comment-child-1", response.getCommentId(), 1L);
    final var responseChild1 = api.create(requestChild1);
    assertThat(responseChild1.getParentCommentId()).isEqualTo(response.getCommentId());

    log.info("[dev] createWithChildrenTest responseChild1 = {}", responseChild1);

    // 첫번째 댓글의 두 번째 댓글
    final var requestChild2 = CommentCreateRequest.of(1L, "my-comment-child-2", response.getCommentId(), 1L);
    final var responseChild2 = api.create(requestChild2);
    assertThat(responseChild2.getParentCommentId()).isEqualTo(response.getCommentId());

    log.info("[dev] createWithChildrenTest responseChild2 = {}", responseChild2);

    // 첫번째 댓글 삭제
    // 자식 댓글이 있으므로 삭제표시만
    assertThat(api.delete(response.getCommentId())).isTrue();
    assertThat(api.read(response.getCommentId()).getDeleted()).isTrue();

    // 자식 댓글 모두 삭제
    assertThat(api.delete(responseChild1.getCommentId())).isTrue();
    assertThat(api.delete(responseChild2.getCommentId())).isTrue();

    // 재귀적 삭제로 첫번째 댓글 조회 시 에러
    assertThatThrownBy(() -> api.read(response.getCommentId()))
      .isInstanceOf(HttpServerErrorException.InternalServerError.class); // 서버에서 NoSuchElementException이 발생함
  }

  @DisplayName("readTest")
  @ParameterizedTest
  @MethodSource("createRequests")
  void readTest(CommentCreateRequest request) {
    final var created = api.create(request);
    final var response = api.read(created.getCommentId());

    assertThat(response).isNotNull();
    assertThat(response.getCommentId()).isEqualTo(created.getCommentId());
    assertThat(api.delete(created.getCommentId())).isTrue();

    log.info("[dev] readTest response = {}", response);
  }

  @DisplayName("create -> delete flow")
  @Test
  void createDeleteFlow() {
    final var created = api.create(CommentCreateRequest.of(1L, "flow-content", null, 1L));

    assertThat(api.delete(created.getCommentId())).isTrue();

    log.info("[dev] flow response = {}", created);
  }

  @DisplayName("readWithPagingTest")
  @ParameterizedTest
  @MethodSource("readWithPagingRequests")
  void readWithPagingTest(Long articleId, Long page, Long pageSize) {
    final var response = api.readWithPaging(articleId, page, pageSize);

    assertThat(response).isNotNull();
    assertThat(response.getComments()).isNotNull().hasSizeLessThanOrEqualTo(pageSize.intValue());

    log.info("[dev] readWithPagingTest comments = {}", response.getComments());
  }

  @DisplayName("readWithScrollTest")
  @ParameterizedTest
  @MethodSource("readWithScrollRequests")
  void readWithScrollTest(Long articleId, Long pageSize) {
    final var firstPage = api.readWithScroll(articleId, pageSize);
    assertThat(firstPage).isNotNull().hasSizeLessThanOrEqualTo(pageSize.intValue());

    if (firstPage.isEmpty()) return;

    final var lastParentCommentId = firstPage.getLast().getParentCommentId();
    final var lastCommentId = firstPage.getLast().getCommentId();

    final var nextPage = api.readWithScroll(articleId, pageSize, lastParentCommentId, lastCommentId);
    assertThat(nextPage)
      .isNotNull()
      .hasSizeLessThanOrEqualTo(pageSize.intValue())
      .allSatisfy(comment -> assertThat(comment.getParentCommentId()).isGreaterThanOrEqualTo(lastParentCommentId));

    log.info("[dev] readWithScrollTest nextPage = {}", nextPage);
  }

  private static Stream<CommentCreateRequest> createRequests() {
    return Stream.of(
      CommentCreateRequest.of(1L, "my-comment-1", null, 1L),
      CommentCreateRequest.of(1L, "my-comment-2", null, 1L)
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
