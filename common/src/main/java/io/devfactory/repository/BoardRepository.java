package io.devfactory.repository;

import io.devfactory.domain.Board;
import io.devfactory.domain.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {

  List<Board> findBoardsByMember(Member member);

}
