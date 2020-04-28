package io.devfactory;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication
public class BoardServerBatchApplication {

  public static void main(String[] args) {
    SpringApplication.run(BoardServerBatchApplication.class, args);
  }

}
