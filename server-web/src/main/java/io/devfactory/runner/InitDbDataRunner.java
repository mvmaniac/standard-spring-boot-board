package io.devfactory.runner;

import io.devfactory.domain.Board;
import io.devfactory.domain.Member;
import io.devfactory.domain.enums.BoardType;
import io.devfactory.repository.BoardRepository;
import io.devfactory.repository.MemberRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class InitDbDataRunner implements ApplicationRunner {

  private final MemberRepository memberRepository;
  private final BoardRepository boardRepository;

  private Random random = new Random();

  @Override
  public void run(ApplicationArguments args) {

    final Member dev1 = createMember("dev1", "dev1357", "dev1@gmail.com");
    final Member dev2 = createMember("dev2", "dev2468", "dev2@gmail.com");
    final List<Member> members = List.of(dev1, dev2);

    memberRepository.saveAll(members);

    final List<Board> boards = new ArrayList<>();

    // @formatter:off
        IntStream.rangeClosed(1, 200).forEach(idx -> {
            Member selectMember = members.get(random.nextInt(2));
            boards.add(createBoard(idx, selectMember));
        });
        // @formatter:on

    boardRepository.saveAll(boards);
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

  private Board createBoard(int idx, Member member) {
    // @formatter:off
        return Board.create()
            .title("게시글 "+ idx)
            .subTitle("순서 "+ idx)
            .content("콘텐츠")
            .boardType(BoardType.FREE)
            .member(member)
            .build();
        // @formatter:on
  }

}
