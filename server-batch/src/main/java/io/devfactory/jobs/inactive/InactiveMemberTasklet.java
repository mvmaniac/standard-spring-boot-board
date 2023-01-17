package io.devfactory.jobs.inactive;

import io.devfactory.domain.Member;
import io.devfactory.domain.enums.Status;
import io.devfactory.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Component
public class InactiveMemberTasklet implements Tasklet {

  private final MemberRepository memberRepository;

  @Override
  public RepeatStatus execute(@NonNull StepContribution contribution, ChunkContext chunkContext) {
    // reader
    final Date nowDate = (Date) chunkContext.getStepContext().getJobParameters().get("nowDate");
    final LocalDateTime now = LocalDateTime.ofInstant(nowDate.toInstant(), ZoneId.systemDefault());

    final List<Member> activeMembers = memberRepository
      .findMembersByUpdatedDateBeforeAndStatusEquals(now.minusYears(1), Status.ACTIVE);

    // processor
    final List<Member> inactiveMembers = activeMembers.stream()
      .map(Member::setInactive)
      .toList();

    // writer
    memberRepository.saveAll(inactiveMembers);

    return RepeatStatus.FINISHED;
  }

}
