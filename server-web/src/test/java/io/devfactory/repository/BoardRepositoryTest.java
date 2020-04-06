package io.devfactory.repository;

import io.devfactory.domain.Board;
import io.devfactory.domain.BoardType;
import io.devfactory.domain.Member;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestConstructor;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.test.context.TestConstructor.AutowireMode.ALL;

@RequiredArgsConstructor
@TestConstructor(autowireMode = ALL)
@TestInstance(PER_CLASS)
@DataJpaTest
class BoardRepositoryTest {

  private final BoardRepository boardRepository;
  private final MemberRepository memberRepository;

  private Member dev;
  private Member admin;

  @BeforeAll
  void setUp() {
    dev = createMember("dev", "dev1234", "dev@gmail.com");
    admin = createMember("admin", "admin1234", "admin@gmail.com");

    memberRepository.saveAll(List.of(dev, admin));
  }

  @DisplayName("회원이 작성한 게시글을 찾을 수 있다")
  @Test
  void 회원이_작성한_게시글을_찾을_수_있다() {
    // given
    final Board devBoard = createBoard("자유게시판", BoardType.FREE, dev);
    final Board adminBoard = createBoard("공지사항", BoardType.NOTICE, admin);

    boardRepository.saveAll(List.of(devBoard, adminBoard));

    // when
    final List<Board> boards = boardRepository.findBoardsByMember(dev);

    // then
    // @formatter:off
    assertThat(boards).isNotEmpty().hasSize(1);
    assertThat(boards)
      .first()
      .extracting("title", "boardType")
      .containsOnly(devBoard.getTitle(), devBoard.getBoardType())
    ;
    // @formatter:on
  }

  private Board createBoard(String title, BoardType boardType, Member member) {
    // @formatter:off
    return Board.create()
      .title(title)
      .subTitle("서브 타이틀")
      .content("콘텐츠")
      .boardType(boardType)
      .member(member)
      .build();
    // @formatter:on
  }

  private Member createMember(String name, String password, String email) {
    // @formatter:off
    return Member.create()
        .name(name)
        .password(password)
        .email(email)
        .build();
    // @formatter:on
  }

}
