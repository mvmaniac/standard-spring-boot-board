package io.devfactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class BoardServerRestApplication {

  public static void main(String[] args) {
    SpringApplication.run(BoardServerRestApplication.class, args);
  }

}
