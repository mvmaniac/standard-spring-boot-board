package io.devfactory.like.api;

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
  ArticleLikeApiClient articleLikeApiClient(RestClient restClient, Environment environment) {
    return new ArticleLikeApiClient(restClient, environment);
  }

}
