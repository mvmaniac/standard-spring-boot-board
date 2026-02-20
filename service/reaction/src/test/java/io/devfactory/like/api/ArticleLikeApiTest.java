package io.devfactory.like.api;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@ActiveProfiles("test")
@Import(RestClientTestConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ArticleLikeApiTest {

  @Autowired
  private ArticleLikeApiClient api;

  @DisplayName("likeAndUnlikeTest")
  @ParameterizedTest
  @CsvSource({"pessimistic-lock-1", "pessimistic-lock-2", "optimistic-lock"})
  void likeAndUnlikeTest(String lockType) {
    final var articleId = 9999L;

    api.like(articleId, 1L, lockType);
    api.like(articleId, 2L, lockType);
    api.like(articleId, 3L, lockType);

    final var response1 = api.read(articleId, 1L);
    final var response2 = api.read(articleId, 2L);
    final var response3 = api.read(articleId, 3L);

    log.info("[dev] response1 = {}", response1);
    log.info("[dev] response3 = {}", response3);
    log.info("[dev] response2 = {}", response2);

    assertThat(api.count(articleId)).isEqualTo(3);

    assertThat(api.unlike(articleId, 1L, lockType)).isTrue();
    assertThat(api.unlike(articleId, 2L, lockType)).isTrue();
    assertThat(api.unlike(articleId, 3L, lockType)).isTrue();

    assertThat(api.count(articleId)).isZero();
  }

  @DisplayName("likePerformanceTest")
  @Test
  void likePerformanceTest() throws InterruptedException {
    final var executorService = Executors.newFixedThreadPool(100);
    // mybatis랑 같이 쓰다보니 아래 순서대로 실행해야 함
    this.startCountDownLatch(executorService, 2222L, "pessimistic-lock-2");
    this.startCountDownLatch(executorService, 1111L, "pessimistic-lock-1");
    this.startCountDownLatch(executorService, 3333L, "optimistic-lock");
  }

  private void startCountDownLatch(
    ExecutorService executorService,
    Long articleId,
    String lockType
  ) throws InterruptedException {
    final var latch = new CountDownLatch(3000);
    log.info("[dev] {} start", lockType);

    api.like(articleId, 1L, lockType);

    final var start = System.nanoTime();

    for (int i = 0; i < 3000; i++) {
      final var userId = i + 2L;
      executorService.submit(() -> {
        api.like(articleId, userId, lockType);
        latch.countDown();
      });
    }

    latch.await();

    final var end = System.nanoTime();

    log.info("[dev] lockType = {}, time = {}ms", lockType, (end - start) / 1000000);

    Long count = api.count(articleId);
    log.info("[dev] count = {}", count);

    log.info("[dev] {} end", lockType);
  }

}
