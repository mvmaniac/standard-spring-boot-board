package io.devfactory.view.controller;

import io.devfactory.view.service.ArticleViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ArticleViewController {

  private final ArticleViewService articleViewService;

  @PostMapping("/v1/article-views/articles/{articleId}/users/{userId}")
  public Long increase(@PathVariable Long articleId, @PathVariable Long userId) {
    return articleViewService.increase(articleId, userId);
  }

  @GetMapping("/v1/article-views/articles/{articleId}/count")
  public Long count(@PathVariable Long articleId) {
    return articleViewService.count(articleId);
  }

}
