package io.devfactory.article.repository;

import io.devfactory.article.entity.BoardArticleCount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardArticleCountRepository extends JpaRepository<BoardArticleCount, Long> {
}
