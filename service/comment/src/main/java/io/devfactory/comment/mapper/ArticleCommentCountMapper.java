package io.devfactory.comment.mapper;

import org.apache.ibatis.annotations.Param;

public interface ArticleCommentCountMapper {

  int upsertArticleCommentCount(@Param("articleId") Long articleId);

  int incrementArticleCommentCount(@Param("articleId") Long articleId);

  int decreaseArticleCommentCount(@Param("articleId") Long articleId);

}
