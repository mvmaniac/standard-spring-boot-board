package io.devfactory.comment;

import io.devfactory.comment.entity.Comment;
import io.devfactory.comment.entity.CommentPath;
import io.devfactory.comment.entity.CommentV2;
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
class DataInitializerV2Test {

  // 총 1200만건 삽입
  private static final int BULK_INSERT_SIZE = 2000;
  private static final int EXECUTE_COUNT = 6000;

  // path 설정
  private static final String CHARSET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
  private static final int DEPTH_CHUNK_SIZE = 5;

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
          int start = i * BULK_INSERT_SIZE;
          int end = (i + 1) * BULK_INSERT_SIZE;

          executorService.submit(() -> {
            try {
              insert(start, end);
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

  void insert(int start, int end) {
    transactionTemplate.executeWithoutResult(_ -> {
      for (int i = start; i < end; i++) {
        CommentV2 comment = CommentV2.create(
            snowflake.nextId(),
            "content",
            1L,
            1L,
            this.toPath(i)
        );
        entityManager.persist(comment);
      }
    });
  }

  private CommentPath toPath(int value) {
    final var path = new StringBuilder();

    for (int i = 0; i < DEPTH_CHUNK_SIZE; i++) {
      path.append(CHARSET.charAt(value % CHARSET.length()));
      value /= CHARSET.length();
    }

    return CommentPath.create(path.reverse().toString());
  }

}
