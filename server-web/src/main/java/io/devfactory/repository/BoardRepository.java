package io.devfactory.repository;

import io.devfactory.domain.Board;
import io.devfactory.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {

  List<Board> findBoardsByMember(Member member);

}
