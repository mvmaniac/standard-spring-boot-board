package io.devfactory.jobs.inactive;

import static java.util.stream.Collectors.toList;

import io.devfactory.domain.Member;
import io.devfactory.domain.enums.Status;
import io.devfactory.repository.MemberRepository;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class InactiveMemberTasklet implements Tasklet {

  private final MemberRepository memberRepository;

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
    // reader
    final Date nowDate = (Date) chunkContext.getStepContext().getJobParameters().get("nowDate");
    final LocalDateTime now = LocalDateTime.ofInstant(nowDate.toInstant(), ZoneId.systemDefault());

    final List<Member> activeMembers = memberRepository
      .findMembersByUpdatedDateBeforeAndStatusEquals(now.minusYears(1), Status.ACTIVE);

    // processor
    final List<Member> inactiveMembers = activeMembers.stream()
      .map(Member::setInactive)
      .collect(toList());

    // writer
    memberRepository.saveAll(inactiveMembers);

    return RepeatStatus.FINISHED;
  }

}
