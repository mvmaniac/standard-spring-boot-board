package io.devfactory.article.api;

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
  ArticleApiClient articleApiClient(RestClient restClient, Environment environment) {
    return new ArticleApiClient(restClient, environment);
  }

}
