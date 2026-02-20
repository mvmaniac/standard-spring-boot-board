package io.devfactory.view.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ArticleViewCountRepository {

  // view::article::{article_id}::view_count
  private static final String KEY_FORMAT = "view::article::%s::view_count";

  private final StringRedisTemplate redisTemplate;

  public Long read(Long articleId) {
    final var result = redisTemplate.opsForValue().get(this.generateKey(articleId));
    return result == null ? 0L : Long.parseLong(result);
  }

  public Long increase(Long articleId) {
    return redisTemplate.opsForValue().increment(this.generateKey(articleId));
  }

  private String generateKey(Long articleId) {
    return KEY_FORMAT.formatted(articleId);
  }

}
