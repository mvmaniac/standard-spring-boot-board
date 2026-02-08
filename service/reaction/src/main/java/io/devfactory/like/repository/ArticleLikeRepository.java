package io.devfactory.like.repository;

import io.devfactory.like.entity.ArticleLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArticleLikeRepository extends JpaRepository<ArticleLike, Long> {

  Optional<ArticleLike> findByArticleIdAndUserId(Long articleId, Long userId);

}
