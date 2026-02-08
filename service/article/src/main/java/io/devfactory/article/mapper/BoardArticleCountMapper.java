package io.devfactory.article.mapper;

import org.apache.ibatis.annotations.Param;

public interface BoardArticleCountMapper {

  int initBoardArticleCount(@Param("boardId") Long boardId);

  int incrementBoardArticleCount(@Param("boardId") Long boardId);

  int decreaseBoardArticleCount(@Param("boardId") Long boardId);

}
