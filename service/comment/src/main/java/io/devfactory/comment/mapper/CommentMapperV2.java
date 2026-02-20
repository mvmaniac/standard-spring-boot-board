package io.devfactory.comment.mapper;

import io.devfactory.comment.mapper.model.CommentRowV2;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

public interface CommentMapperV2 {

  Optional<String> findDescendantsTopPath(@Param("articleId") Long articleId,
    @Param("pathPrefix") String pathPrefix
  );

  List<CommentRowV2> findCommentsWithPaging(@Param("articleId") Long articleId,
    @Param("offset") Long offset, @Param("limit") Long limit
  );

  Long countCommentsWithLimit(@Param("articleId") Long articleId, @Param("limit") Long limit);

  List<CommentRowV2> findCommentsWithScroll(@Param("articleId") Long articleId,
    @Param("limit") Long limit
  );

  List<CommentRowV2> findCommentsNextWithScroll(@Param("articleId") Long articleId,
    @Param("limit") Long limit, @Param("lastPath") String lastPath
  );

}
