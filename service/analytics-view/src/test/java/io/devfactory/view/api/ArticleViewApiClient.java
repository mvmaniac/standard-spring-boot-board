package io.devfactory.view.api;

import org.springframework.core.env.Environment;
import org.springframework.web.client.RestClient;

class ArticleViewApiClient {

  private final RestClientSupport restClientSupport;
  private final Environment environment;
  private String baseUrl;

  public ArticleViewApiClient(RestClient restClient, Environment environment) {
    this.restClientSupport = new RestClientSupport(restClient);
    this.environment = environment;
  }

  public void increase(Long articleId, Long userId) {
    restClientSupport.post(baseUrl() + "/v1/article-views/articles/{articleId}/users/{userId}", Void.class, articleId, userId);
  }

  public Long count(Long articleId) {
    return restClientSupport.get(baseUrl() + "/v1/article-views/articles/{articleId}/count", Long.class, articleId);
  }

  private String baseUrl() {
    if (baseUrl == null) {
      String port = environment.getProperty("local.server.port");
      if (port == null) {
        throw new IllegalStateException("local.server.port is not available. Did the server start?");
      }
      baseUrl = "http://localhost:" + port;
    }
    return baseUrl;
  }

}
