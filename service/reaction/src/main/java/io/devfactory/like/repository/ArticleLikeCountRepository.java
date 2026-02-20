package io.devfactory.like.repository;

import io.devfactory.like.entity.ArticleLikeCount;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface ArticleLikeCountRepository extends JpaRepository<ArticleLikeCount, Long> {

  // select ... for update
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<ArticleLikeCount> findLockedByArticleId(Long articleId);

}
