package io.devfactory.like.controller;

import io.devfactory.like.dto.response.ArticleLikeResponse;
import io.devfactory.like.service.ArticleLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class ArticleLikeController {

  private final ArticleLikeService articleLikeService;

  @GetMapping("/v1/article-likes/articles/{articleId}/users/{userId}")
  public ArticleLikeResponse read(@PathVariable Long articleId, @PathVariable Long userId) {
    return articleLikeService.read(articleId, userId);
  }

  @GetMapping("/v1/article-likes/articles/{articleId}/count")
  public Long count(@PathVariable Long articleId) {
    return articleLikeService.count(articleId);
  }

  @PostMapping("/v1/article-likes/articles/{articleId}/users/{userId}/pessimistic-lock-1")
  public void likeWithPessimisticLock1(@PathVariable Long articleId, @PathVariable Long userId) {
    articleLikeService.likeWithPessimisticLock1(articleId, userId);
  }

  @DeleteMapping("/v1/article-likes/articles/{articleId}/users/{userId}/pessimistic-lock-1")
  public void unlikeWithPessimisticLock1(@PathVariable Long articleId, @PathVariable Long userId) {
    articleLikeService.unlikeWithPessimisticLock1(articleId, userId);
  }

  @PostMapping("/v1/article-likes/articles/{articleId}/users/{userId}/pessimistic-lock-2")
  public void likePessimisticLock2(@PathVariable Long articleId, @PathVariable Long userId) {
    articleLikeService.likeWithPessimisticLock2(articleId, userId);
  }

  @DeleteMapping("/v1/article-likes/articles/{articleId}/users/{userId}/pessimistic-lock-2")
  public void unlikePessimisticLock2(@PathVariable Long articleId, @PathVariable Long userId) {
    articleLikeService.unlikeWithPessimisticLock2(articleId, userId);
  }

  @PostMapping("/v1/article-likes/articles/{articleId}/users/{userId}/optimistic-lock")
  public void likeWithOptimisticLock(@PathVariable Long articleId, @PathVariable Long userId) {
    articleLikeService.likeWithOptimisticLock(articleId, userId);
  }

  @DeleteMapping("/v1/article-likes/articles/{articleId}/users/{userId}/optimistic-lock")
  public void unlikeWithOptimisticLock(@PathVariable Long articleId, @PathVariable Long userId) {
    articleLikeService.unlikeWithOptimisticLock(articleId, userId);
  }

}
