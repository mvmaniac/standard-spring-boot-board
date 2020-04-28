package io.devfactory.jobs.inactive.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InactiveMemberJobListener implements JobExecutionListener {

  @Override
  public void beforeJob(JobExecution jobExecution) {
    log.debug("[dev] before job...");
  }

  @Override
  public void afterJob(JobExecution jobExecution) {
    log.debug("[dev] after job...");
  }

}
