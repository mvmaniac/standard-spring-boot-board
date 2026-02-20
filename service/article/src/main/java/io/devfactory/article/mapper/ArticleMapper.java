package io.devfactory.article.mapper;

import io.devfactory.article.mapper.model.ArticleRow;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ArticleMapper {

  List<ArticleRow> findArticlesWithPaging(
    @Param("boardId") Long boardId,
    @Param("offset") Long offset,
    @Param("limit") Long limit
  );

  Long countArticlesWithLimit(@Param("boardId") Long boardId, @Param("limit") Long limit);

  List<ArticleRow> findArticlesWithScroll(
    @Param("boardId") Long boardId,
    @Param("limit") Long limit
  );

  List<ArticleRow> findArticlesNextWithScroll(
    @Param("boardId") Long boardId,
    @Param("limit") Long limit,
    @Param("lastArticleId") Long lastArticleId
  );

}
