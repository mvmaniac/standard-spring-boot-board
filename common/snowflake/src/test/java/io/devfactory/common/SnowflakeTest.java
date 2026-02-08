package io.devfactory.common;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class SnowflakeTest {

  private final Snowflake snowflake = new Snowflake();

  @Test
  void nextIdTest() throws ExecutionException, InterruptedException {
    // given
    try (final var executorService = Executors.newFixedThreadPool(10)) {
      final var futures = new ArrayList<Future<List<Long>>>();
      final var repeatCount = 1000;
      final var idCount = 1000;

      // when
      for (int i = 0; i < repeatCount; i++) {
        futures.add(executorService.submit(() -> generateIdList(snowflake, idCount)));
      }

      // then
      final var result = new ArrayList<Long>();
      for (final var future : futures) {
        final var idList = future.get();
        for (int i = 1; i < idList.size(); i++) {
          assertThat(idList.get(i)).isGreaterThan(idList.get(i - 1));
        }
        result.addAll(idList);
      }
      assertThat(result.stream().distinct().count()).isEqualTo(repeatCount * idCount);

      executorService.shutdown();
    }
  }

  List<Long> generateIdList(Snowflake snowflake, int count) {
    final var idList = new ArrayList<Long>();
    while (count-- > 0) {
      idList.add(snowflake.nextId());
    }
    return idList;
  }

  @Test
  void nextIdPerformanceTest() throws InterruptedException {
    // given
    try (final var executorService = Executors.newFixedThreadPool(10)) {
      final var repeatCount = 1000;
      final var idCount = 1000;
      final var latch = new CountDownLatch(repeatCount);

      // when
      final var start = System.nanoTime();
      for (int i = 0; i < repeatCount; i++) {
        executorService.submit(() -> {
          generateIdList(snowflake, idCount);
          latch.countDown();
        });
      }

      latch.await();

      final var end = System.nanoTime();
      log.debug("[dev] times = {} ms", (end - start) / 1_000_000);

      executorService.shutdown();
    }
  }

}
