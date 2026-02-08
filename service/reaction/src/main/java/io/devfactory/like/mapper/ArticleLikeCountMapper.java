package io.devfactory.like.mapper;

import org.apache.ibatis.annotations.Param;

public interface ArticleLikeCountMapper {

  int upsertLikeCount(@Param("articleId") Long articleId);

  int incrementLikeCount(@Param("articleId") Long articleId);

  int decreaseLikeCount(@Param("articleId") Long articleId);

}
