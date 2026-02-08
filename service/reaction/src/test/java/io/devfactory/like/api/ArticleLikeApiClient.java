package io.devfactory.like.api;

import io.devfactory.like.dto.response.ArticleLikeResponse;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestClient;

class ArticleLikeApiClient {

  private final RestClientSupport restClientSupport;
  private final Environment environment;
  private String baseUrl;

  public ArticleLikeApiClient(RestClient restClient, Environment environment) {
    this.restClientSupport = new RestClientSupport(restClient);
    this.environment = environment;
  }

  public Long count(Long articleId) {
    return restClientSupport.get(baseUrl() + "/v1/article-likes/articles/{articleId}/count", Long.class, articleId);
  }

  public void like(Long articleId, Long userId, String lockType) {
    restClientSupport.post(baseUrl() + "/v1/article-likes/articles/{articleId}/users/{userId}/{lockType}", Void.class, articleId, userId, lockType);
  }

  public boolean unlike(Long articleId, Long userId, String lockType) {
    return restClientSupport.delete(baseUrl() + "/v1/article-likes/articles/{articleId}/users/{userId}/{lockType}", articleId, userId, lockType)
        .getStatusCode()
        .is2xxSuccessful();
  }

  public ArticleLikeResponse read(Long articleId, Long userId) {
    return restClientSupport.get(baseUrl() + "/v1/article-likes/articles/{articleId}/users/{userId}", ArticleLikeResponse.class, articleId, userId);
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
