package io.devfactory.repository;

import io.devfactory.domain.Board;
import io.devfactory.domain.Member;
import java.util.List;
import io.devfactory.domain.projection.BoardOnlyContainsTitle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

@RepositoryRestResource(excerptProjection = BoardOnlyContainsTitle.class)
public interface BoardRepository extends JpaRepository<Board, Long> {

  List<Board> findBoardsByMember(Member member);

  @RestResource(path = "query")
  List<Board> findByTitle(@Param("title") String title);

}
