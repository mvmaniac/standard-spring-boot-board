package io.devfactory.view.api;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@ActiveProfiles("test")
@Import(RestClientTestConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ArticleViewApiTest {

  @Autowired
  private ArticleViewApiClient api;

  @DisplayName("increaseTest")
  @Test
  void increaseTest() throws InterruptedException {
    CountDownLatch latch;

    try (final var executorService = Executors.newFixedThreadPool(100)) {
      latch = new CountDownLatch(10000);

      for (int i = 0; i < 10000; i++) {
        executorService.submit(() -> {
          api.increase(4L, 1L);
          latch.countDown();
        });
      }
    }

    latch.await();

    final var count = api.count(4L);
    log.info("[dev] count = {}", count);
  }

}
