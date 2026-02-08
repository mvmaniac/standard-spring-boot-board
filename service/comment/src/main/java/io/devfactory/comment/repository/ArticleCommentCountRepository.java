package io.devfactory.comment.repository;

import io.devfactory.comment.entity.ArticleCommentCount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleCommentCountRepository extends JpaRepository<ArticleCommentCount, Long> {
}
