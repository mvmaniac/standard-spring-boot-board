package io.devfactory.jobs.inactive;

import io.devfactory.domain.Member;
import io.devfactory.domain.enums.Status;
import io.devfactory.jobs.inactive.listener.InactiveMemberJobListener;
import io.devfactory.jobs.inactive.listener.InactiveMemberStepListener;
import io.devfactory.jobs.readers.QueueItemReader;
import io.devfactory.repository.MemberRepository;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class InactiveMemberJobConfig {

  private static final int CHUNK_SIZE = 10;
  private final EntityManagerFactory entityManagerFactory;

  private final JobRepository jobRepository;
  private final PlatformTransactionManager platformTransactionManager;

  private final MemberRepository memberRepository;

  @Bean
  public TaskExecutor taskExecutor() {
    final var simpleAsyncTaskExecutor = new SimpleAsyncTaskExecutor("Batch-Task");
    simpleAsyncTaskExecutor.setConcurrencyLimit(2);
    return simpleAsyncTaskExecutor;
  }

  @Bean
  public Job inactiveMemberJob(InactiveMemberJobListener inactiveMemberJobListener,
    Step inactiveMemberStep) {
    return new JobBuilder("inactiveMemberJob", jobRepository)
      .preventRestart()
      .listener(inactiveMemberJobListener)
      .start(inactiveMemberStep)
      .build();
  }

  @Bean
  public Job inactiveMemberJobFlow(InactiveMemberJobListener inactiveMemberJobListener,
    Flow inactiveMemberFlow) {
    return new JobBuilder("inactiveMemberJobFlow", jobRepository)
      .preventRestart()
      .listener(inactiveMemberJobListener)
      .start(inactiveMemberFlow)
      .end()
      .build();
  }

  @Bean
  public Step inactiveMemberStep(InactiveMemberStepListener inactiveMemberStepListener,
    JpaPagingItemReader<Member> inactiveMemberJpaReader) {
    return new StepBuilder("inactiveMemberStep", jobRepository)
      .<Member, Member>chunk(CHUNK_SIZE, platformTransactionManager)
      .reader(inactiveMemberJpaReader)
      .processor(this.inactiveMemberProcessor())
      .writer(this.inactiveMemberJpaWriter())
      .listener(inactiveMemberStepListener)
      .taskExecutor(taskExecutor())
      .build();
  }

  @Bean
  public Flow inactiveMemberFlow(Step inactiveMemberStep) {
    FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("inactiveMemberFlow");
    return flowBuilder
      .start(new InactiveMemberJobExecutionDecider())
      .on(FlowExecutionStatus.FAILED.getName())
      .end()
      .on(FlowExecutionStatus.COMPLETED.getName())
        .to(inactiveMemberStep)
      .end();
  }

  // 이미 구현되어 있는 ListItemReader 를 사용하는 방법 
  @Bean
  @StepScope
  public ListItemReader<Member> inactiveMemberListItemReader() {
    final List<Member> oldMembers = memberRepository
      .findMembersByUpdatedDateBeforeAndStatusEquals(LocalDateTime.now().minusYears(1),
        Status.ACTIVE);

    return new ListItemReader<>(oldMembers);
  }

  // QueueItemReader 라는 ItemReader 를 사용하는 방법
  @Bean
  @StepScope
  public QueueItemReader<Member> inactiveMemberCustomReader() {
    final List<Member> oldMembers = memberRepository
      .findMembersByUpdatedDateBeforeAndStatusEquals(LocalDateTime.now().minusYears(1),
        Status.ACTIVE);

    return new QueueItemReader<>(oldMembers);
  }

  @Bean(destroyMethod = "")
  @StepScope
  public JpaPagingItemReader<Member> inactiveMemberJpaReader(
    @Value("#{jobParameters[nowDate]}") Date nowDate) {

    @SuppressWarnings("squid:S110") final JpaPagingItemReader<Member> jpaPagingItemReader = new JpaPagingItemReader<>() {
      @Override
      public int getPage() {
        return 0;
      }
    };

    final LocalDateTime now = LocalDateTime.ofInstant(nowDate.toInstant(), ZoneId.systemDefault());

    jpaPagingItemReader.setQueryString(
      "select m from Member as m where m.updatedDate < :updatedDate and m.status = :status");

    Map<String, Object> paramMap = new HashMap<>();
    paramMap.put("updatedDate", now.minusYears(1));
    paramMap.put("status", Status.ACTIVE);

    jpaPagingItemReader.setParameterValues(paramMap);
    jpaPagingItemReader.setEntityManagerFactory(entityManagerFactory);
    jpaPagingItemReader.setPageSize(CHUNK_SIZE);

    return jpaPagingItemReader;
  }

  private ItemProcessor<Member, Member> inactiveMemberProcessor() {
    return m -> {
      log.debug("[dev] processor....1 {}", m);
      m.setInactive();
      log.debug("[dev] processor....2");
      return m;
    };
  }

  private ItemWriter<Member> inactiveMemberItemWriter() {
    return memberRepository::saveAll;
  }

  private JpaItemWriter<Member> inactiveMemberJpaWriter() {
    final JpaItemWriter<Member> jpaItemWriter = new JpaItemWriter<>();
    jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
    //jpaItemWriter.setUsePersist(true);
    return jpaItemWriter;
  }

}
