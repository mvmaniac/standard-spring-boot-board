package io.devfactory.comment.api;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestClient;

@TestConfiguration(proxyBeanMethods = false)
public class RestClientTestConfig {

  @Bean
  public RestClient restClient() {
    return RestClient.builder().build();
  }

  @Bean
  CommentApiClient commentApiClient(RestClient restClient, Environment environment) {
    return new CommentApiClient(restClient, environment);
  }

  @Bean
  CommentApiV2Client commentApiV2Client(RestClient restClient, Environment environment) {
    return new CommentApiV2Client(restClient, environment);
  }

}
