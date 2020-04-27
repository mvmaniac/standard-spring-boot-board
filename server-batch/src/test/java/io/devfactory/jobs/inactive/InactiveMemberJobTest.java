package io.devfactory.jobs.inactive;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.TestConstructor.AutowireMode.ALL;

import io.devfactory.domain.enums.Status;
import io.devfactory.repository.MemberRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

@RequiredArgsConstructor
@TestConstructor(autowireMode = ALL)
@SpringBootTest
class InactiveMemberJobTest {

  private final JobLauncherTestUtils jobLauncherTestUtils;

  private final MemberRepository memberRepository;

  @DisplayName("휴면 회원 으로 전환 할  있다")
  @Test
  void 휴면_회원_으로_전환_할_수_있다() throws Exception {

    final JobExecution jobExecution = jobLauncherTestUtils.launchJob();

    assertThat(BatchStatus.COMPLETED).isEqualTo(jobExecution.getStatus());
    assertThat(0).isEqualTo(memberRepository.findMembersByUpdatedDateBeforeAndStatusEquals(
      LocalDateTime.now().minusYears(1L), Status.ACTIVE).size());
  }

}
