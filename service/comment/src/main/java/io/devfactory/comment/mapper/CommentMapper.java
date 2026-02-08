package io.devfactory.comment.mapper;

import io.devfactory.comment.mapper.model.CommentRow;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommentMapper {

  Long countCommentChildren(@Param("articleId") Long articleId,
      @Param("parentCommentId") Long parentCommentId, @Param("limit") Long limit);

  List<CommentRow> findCommentsWithPaging(@Param("articleId") Long articleId,
      @Param("offset") Long offset,
      @Param("limit") Long limit);

  Long countCommentsWithLimit(@Param("articleId") Long articleId, @Param("limit") Long limit);

  List<CommentRow> findCommentsWithScroll(@Param("articleId") Long articleId,
      @Param("limit") Long limit);

  List<CommentRow> findCommentsNextWithScroll(@Param("articleId") Long articleId,
      @Param("limit") Long limit,
      @Param("lastParentCommentId") Long lastParentCommentId,
      @Param("lastCommentId") Long lastCommentId);

}
