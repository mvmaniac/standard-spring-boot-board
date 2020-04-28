package io.devfactory.jobs.inactive.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InactiveMemberStepListener {

  @BeforeStep
  public void beforeStep(StepExecution stepExecution) {
    log.debug("[dev] before step...");
  }

  @AfterStep
  public void afterStep(StepExecution stepExecution) {
    log.debug("[dev] after step...");
  }

}
