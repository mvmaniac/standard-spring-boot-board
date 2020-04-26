package io.devfactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class ServerWebApplication {

  public static void main(String[] args) {
    SpringApplication.run(ServerWebApplication.class, args);
  }

}
