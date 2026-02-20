package io.devfactory.comment.controller;

import io.devfactory.comment.dto.request.CommentCreateRequestV2;
import io.devfactory.comment.dto.response.CommentPageResponse;
import io.devfactory.comment.dto.response.CommentResponse;
import io.devfactory.comment.service.CommentServiceV2;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class CommentControllerV2 {

  private final CommentServiceV2 commentService;

  @GetMapping("/v2/comments")
  public CommentPageResponse readWithPaging(
    @RequestParam("articleId") Long articleId,
    @RequestParam("page") Long page,
    @RequestParam("pageSize") Long pageSize
  ) {
    return commentService.readWithPaging(articleId, page, pageSize);
  }

  @GetMapping("/v2/comments/scroll")
  public List<CommentResponse> readWithScroll(
    @RequestParam("articleId") Long articleId,
    @RequestParam("pageSize") Long pageSize,
    @RequestParam(value = "lastPath", required = false) String lastPath
  ) {
    return commentService.readWithScroll(articleId, pageSize, lastPath);
  }

  @GetMapping("/v2/comments/{commentId}")
  public CommentResponse read(@PathVariable Long commentId) {
    return commentService.read(commentId);
  }

  @PostMapping("/v2/comments")
  public CommentResponse create(@RequestBody CommentCreateRequestV2 request) {
    return commentService.create(request);
  }

  @DeleteMapping("/v2/comments/{commentId}")
  public void delete(@PathVariable Long commentId) {
    commentService.delete(commentId);
  }

  @GetMapping("/v2/comments/articles/{articleId}/count")
  public Long countComments(@PathVariable Long articleId) {
    return commentService.countComments(articleId);
  }

}
