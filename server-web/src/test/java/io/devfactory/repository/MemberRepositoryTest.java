package io.devfactory.repository;

import io.devfactory.domain.Member;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestConstructor;

import java.util.List;

import static io.devfactory.util.FunctionUtils.emptyEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.test.context.TestConstructor.AutowireMode.ALL;

@RequiredArgsConstructor
@TestConstructor(autowireMode = ALL)
@TestInstance(PER_CLASS)
@DataJpaTest
class MemberRepositoryTest {

  private final MemberRepository memberRepository;

  @DisplayName("이메일 주소로 회원을 찾을 수 있다")
  @Test
  void 이메일_주소로_회원을_찾을_수_있다() {
    // given
    final Member dev = createMember("dev", "dev1234", "dev@gmail.com");
    final Member admin = createMember("admin", "admin1234", "admin@gmail.com");

    memberRepository.saveAll(List.of(dev, admin));

    // when
    // @formatter:off
    final Member findMember = memberRepository.findMemberByEmail(admin.getEmail())
        .orElseGet(emptyEntity(Member.create().build()));
    // @formatter:on

    // then
    assertThat(findMember).isNotNull();
    assertThat(findMember.getName()).isEqualTo(admin.getName());
    assertThat(findMember.getPassword()).isEqualTo(admin.getPassword());
    assertThat(findMember.getEmail()).isEqualTo(admin.getEmail());
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
