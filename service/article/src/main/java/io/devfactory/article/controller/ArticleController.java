package io.devfactory.article.controller;

import io.devfactory.article.dto.request.ArticleCreateRequest;
import io.devfactory.article.dto.request.ArticleUpdateRequest;
import io.devfactory.article.dto.response.ArticlePageResponse;
import io.devfactory.article.dto.response.ArticleResponse;
import io.devfactory.article.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ArticleController {

  private final ArticleService articleService;

  @GetMapping("/v1/articles")
  public ArticlePageResponse readWithPaging(
    @RequestParam("boardId") Long boardId,
    @RequestParam("page") Long page,
    @RequestParam("pageSize") Long pageSize
  ) {
    return articleService.readWithPaging(boardId, page, pageSize);
  }

  @GetMapping("/v1/articles/scroll")
  public List<ArticleResponse> readWithScroll(
    @RequestParam("boardId") Long boardId,
    @RequestParam("pageSize") Long pageSize,
    @RequestParam(value = "lastArticleId", required = false) Long lastArticleId
  ) {
    return articleService.readWithScroll(boardId, pageSize, lastArticleId);
  }

  @GetMapping("/v1/articles/{articleId}")
  public ArticleResponse read(@PathVariable Long articleId) {
    return articleService.read(articleId);
  }

  @PostMapping("/v1/articles")
  public ArticleResponse create(@RequestBody ArticleCreateRequest request) {
    return articleService.create(request);
  }

  @PutMapping("/v1/articles/{articleId}")
  public ArticleResponse update(
    @PathVariable Long articleId,
    @RequestBody ArticleUpdateRequest request
  ) {
    return articleService.update(articleId, request);
  }

  @DeleteMapping("/v1/articles/{articleId}")
  public void delete(@PathVariable Long articleId) {
    articleService.delete(articleId);
  }

  @GetMapping("/v1/articles/boards/{boardId}/count")
  public Long countArticles(@PathVariable Long boardId) {
    return articleService.countArticles(boardId);
  }

}
