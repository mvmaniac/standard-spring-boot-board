package io.devfactory.comment.controller;

import io.devfactory.comment.dto.request.CommentCreateRequest;
import io.devfactory.comment.dto.response.CommentPageResponse;
import io.devfactory.comment.dto.response.CommentResponse;
import io.devfactory.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class CommentController {

  private final CommentService commentService;

  @GetMapping("/v1/comments")
  public CommentPageResponse readWithPaging(
      @RequestParam("articleId") Long articleId,
      @RequestParam("page") Long page,
      @RequestParam("pageSize") Long pageSize) {
    return commentService.readWithPaging(articleId, page, pageSize);
  }

  @GetMapping("/v1/comments/scroll")
  public List<CommentResponse> readWithScroll(
      @RequestParam("articleId") Long articleId,
      @RequestParam("pageSize") Long pageSize,
      @RequestParam(value = "lastParentCommentId", required = false) Long lastParentCommentId,
      @RequestParam(value = "lastCommentId", required = false) Long lastCommentId) {
    return commentService.readWithScroll(articleId, pageSize, lastParentCommentId, lastCommentId);
  }

  @GetMapping("/v1/comments/{commentId}")
  public CommentResponse read(@PathVariable Long commentId) {
    return commentService.read(commentId);
  }

  @PostMapping("/v1/comments")
  public CommentResponse create(@RequestBody CommentCreateRequest request) {
    return commentService.create(request);
  }

  @DeleteMapping("/v1/comments/{commentId}")
  public void delete(@PathVariable Long commentId) {
    commentService.delete(commentId);
  }

}
