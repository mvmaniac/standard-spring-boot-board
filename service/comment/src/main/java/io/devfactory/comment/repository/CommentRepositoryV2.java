package io.devfactory.comment.repository;

import io.devfactory.comment.entity.CommentV2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommentRepositoryV2 extends JpaRepository<CommentV2, Long> {

  @Query("select c from CommentV2 c where c.articleId = :articleLid and c.commentPath.path = :path")
  Optional<CommentV2> findByArticleIdAndPath(@Param("articleId") Long articleLid,
    @Param("path") String path
  );

}
