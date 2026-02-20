package io.devfactory.view.api;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

final class RestClientSupport {

  private final RestClient restClient;

  RestClientSupport(RestClient restClient) {
    this.restClient = restClient;
  }

  public <T> T get(String uri, Class<T> responseType, Object... uriVariables) {
    return restClient.get()
      .uri(uri, uriVariables)
      .retrieve()
      .body(responseType);
  }

  public <T> T get(String uri, ParameterizedTypeReference<T> responseType, Object... uriVariables) {
    return restClient.get()
      .uri(uri, uriVariables)
      .retrieve()
      .body(responseType);
  }

  public <T> T post(String uri, Object body, Class<T> responseType) {
    return restClient.post()
      .uri(uri)
      .body(body)
      .retrieve()
      .body(responseType);
  }

  public <T> T post(String uri, Class<T> responseType, Object... uriVariables) {
    return restClient.post()
      .uri(uri, uriVariables)
      .retrieve()
      .body(responseType);
  }

  public <T> T put(String uri, Object body, Class<T> responseType, Object... uriVariables) {
    return restClient.put()
      .uri(uri, uriVariables)
      .body(body)
      .retrieve()
      .body(responseType);
  }

  public ResponseEntity<Void> delete(String uri, Object... uriVariables) {
    return restClient.delete()
      .uri(uri, uriVariables)
      .retrieve()
      .toBodilessEntity();
  }

}
