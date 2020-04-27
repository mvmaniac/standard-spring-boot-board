package io.devfactory.jobs.inactive;

import io.devfactory.domain.Member;
import io.devfactory.domain.enums.Status;
import io.devfactory.jobs.readers.QueueItemReader;
import io.devfactory.repository.MemberRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class InactiveMemberJobConfig {

  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;

  private final MemberRepository memberRepository;

  @Bean
  public Job inactiveMemberJob(Step inactiveMemberStep) {
    return jobBuilderFactory.get("inactiveMemberJob")
      .preventRestart()
      .start(inactiveMemberStep)
      .build();
  }

  @Bean
  public Step inactiveMemberStep() {
    return stepBuilderFactory.get("inactiveMemberStep")
      .<Member, Member>chunk(10)
      .reader(this.inactiveMemberReader())
      .processor(this.inactiveMemberProcessor())
      .writer(this.inactiveMemberWriter())
      .build();
  }

  @StepScope
  @Bean
  public QueueItemReader<Member> inactiveMemberReader() {
    final List<Member> oldMembers = memberRepository
      .findMembersByUpdatedDateBeforeAndStatusEquals(LocalDateTime.now().minusYears(1),
        Status.ACTIVE);

    return new QueueItemReader<>(oldMembers);
  }

  public ItemProcessor<Member, Member> inactiveMemberProcessor() {
    return Member::setInactive;
  }

  public ItemWriter<Member> inactiveMemberWriter() {
    return memberRepository::saveAll;
  }

}
