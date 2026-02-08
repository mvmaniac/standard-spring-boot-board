package io.devfactory.comment;

import io.devfactory.comment.entity.Comment;
import io.devfactory.common.Snowflake;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest
class DataInitializerTest {

  // 총 1200만건 삽입
  private static final int BULK_INSERT_SIZE = 2000;
  private static final int EXECUTE_COUNT = 6000;

  @PersistenceContext
  private EntityManager entityManager;

  @Autowired
  private TransactionTemplate transactionTemplate;

  private final Snowflake snowflake = new Snowflake();
  private final CountDownLatch latch = new CountDownLatch(EXECUTE_COUNT);

  @DisplayName("initialize")
  @Test
  void initialize() {
    assertDoesNotThrow(() -> {
      try (ExecutorService executorService = Executors.newFixedThreadPool(10)) {
        for (int i = 0; i < EXECUTE_COUNT; i++) {
          executorService.submit(() -> {
            try {
              insert();
            } finally {
              latch.countDown();
            }

            log.info("latch.getCount() = {}", latch.getCount());
          });
        }
        latch.await();
      }
    });
  }

  void insert() {
    transactionTemplate.executeWithoutResult(_ -> {
      Comment prev = null;
      for (int i = 0; i < BULK_INSERT_SIZE; i++) {
        final var comment = Comment.create(snowflake.nextId(), "content", i % 2 == 0 ? null : prev.getCommentId(), 1L, 1L);
        prev = comment;
        entityManager.persist(comment);
      }
    });
  }

}
