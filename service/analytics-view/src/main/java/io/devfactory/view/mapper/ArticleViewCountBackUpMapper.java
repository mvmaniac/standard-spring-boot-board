package io.devfactory.view.mapper;

import org.apache.ibatis.annotations.Param;

public interface ArticleViewCountBackUpMapper {

  long selectArticleViewCountById(@Param("articleId") Long articleId);

  int upsertArticleViewCount(
    @Param("articleId") Long articleId,
    @Param("viewCount") Long viewCount
  );

}
