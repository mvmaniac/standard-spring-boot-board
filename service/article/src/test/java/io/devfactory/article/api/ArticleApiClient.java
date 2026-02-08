package io.devfactory.article.api;

import io.devfactory.article.dto.request.ArticleCreateRequest;
import io.devfactory.article.dto.request.ArticleUpdateRequest;
import io.devfactory.article.dto.response.ArticlePageResponse;
import io.devfactory.article.dto.response.ArticleResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestClient;

import java.util.List;

class ArticleApiClient {

  private static final ParameterizedTypeReference<List<ArticleResponse>> ARTICLE_LIST_TYPE = new ParameterizedTypeReference<>() {};

  private final RestClientSupport restClientSupport;
  private final Environment environment;
  private String baseUrl;

  public ArticleApiClient(RestClient restClient, Environment environment) {
    this.restClientSupport = new RestClientSupport(restClient);
    this.environment = environment;
  }

  public ArticleResponse create(ArticleCreateRequest request) {
    return restClientSupport.post(baseUrl() + "/v1/articles", request, ArticleResponse.class);
  }

  public ArticleResponse read(Long articleId) {
    return restClientSupport.get(baseUrl() + "/v1/articles/{articleId}", ArticleResponse.class, articleId);
  }

  public ArticlePageResponse readWithPaging(Long boardId, Long page, Long pageSize) {
    return restClientSupport.get(
        baseUrl() + "/v1/articles?boardId={boardId}&page={page}&pageSize={pageSize}",
        ArticlePageResponse.class,
        boardId,
        page,
        pageSize
    );
  }

  public List<ArticleResponse> readWithScroll(Long boardId, Long pageSize) {
    return restClientSupport.get(
        baseUrl() + "/v1/articles/scroll?boardId={boardId}&pageSize={pageSize}",
        ARTICLE_LIST_TYPE,
        boardId,
        pageSize
    );
  }

  public List<ArticleResponse> readWithScroll(Long boardId, Long pageSize, Long lastArticleId) {
    if (lastArticleId == null) return readWithScroll(boardId, pageSize);

    return restClientSupport.get(
        baseUrl() + "/v1/articles/scroll?boardId={boardId}&pageSize={pageSize}&lastArticleId={lastArticleId}",
        ARTICLE_LIST_TYPE,
        boardId,
        pageSize,
        lastArticleId
    );
  }

  public ArticleResponse update(Long articleId, ArticleUpdateRequest request) {
    return restClientSupport.put(baseUrl() + "/v1/articles/{articleId}", request, ArticleResponse.class, articleId);
  }

  public boolean delete(Long articleId) {
    return restClientSupport.delete(baseUrl() + "/v1/articles/{articleId}", articleId)
        .getStatusCode()
        .is2xxSuccessful();
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
