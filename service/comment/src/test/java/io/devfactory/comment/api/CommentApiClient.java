package io.devfactory.comment.api;

import io.devfactory.comment.dto.request.CommentCreateRequest;
import io.devfactory.comment.dto.response.CommentPageResponse;
import io.devfactory.comment.dto.response.CommentResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestClient;

import java.util.List;

class CommentApiClient {

  private static final ParameterizedTypeReference<List<CommentResponse>> COMMENT_LIST_TYPE =
      new ParameterizedTypeReference<>() {
      };

  private final RestClientSupport restClientSupport;
  private final Environment environment;
  private String baseUrl;

  public CommentApiClient(RestClient restClient, Environment environment) {
    this.restClientSupport = new RestClientSupport(restClient);
    this.environment = environment;
  }

  public CommentResponse create(CommentCreateRequest request) {
    return restClientSupport.post(baseUrl() + "/v1/comments", request, CommentResponse.class);
  }

  public CommentResponse read(Long commentId) {
    return restClientSupport.get(baseUrl() + "/v1/comments/{commentId}", CommentResponse.class, commentId);
  }

  public CommentPageResponse readWithPaging(Long articleId, Long page, Long pageSize) {
    return restClientSupport.get(
        baseUrl() + "/v1/comments?articleId={articleId}&page={page}&pageSize={pageSize}",
        CommentPageResponse.class,
        articleId,
        page,
        pageSize
    );
  }

  public List<CommentResponse> readWithScroll(Long articleId, Long pageSize) {
    return restClientSupport.get(
        baseUrl() + "/v1/comments/scroll?articleId={articleId}&pageSize={pageSize}",
        COMMENT_LIST_TYPE,
        articleId,
        pageSize
    );
  }

  public List<CommentResponse> readWithScroll(Long articleId, Long pageSize, Long lastParentCommentId, Long lastCommentId) {
    if (lastCommentId == null) return readWithScroll(articleId, pageSize);

    return restClientSupport.get(
        baseUrl() + "/v1/comments/scroll?articleId={articleId}&pageSize={pageSize}&lastParentCommentId={lastParentCommentId}&lastCommentId={lastCommentId}",
        COMMENT_LIST_TYPE,
        articleId,
        pageSize,
        lastParentCommentId,
        lastCommentId
    );
  }

  public boolean delete(Long commentId) {
    return restClientSupport.delete(baseUrl() + "/v1/comments/{commentId}", commentId)
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
