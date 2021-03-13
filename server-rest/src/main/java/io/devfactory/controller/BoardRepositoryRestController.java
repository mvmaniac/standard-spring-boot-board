package io.devfactory.controller;

import io.devfactory.domain.Board;
import io.devfactory.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.PagedModel.PageMetadata;
import org.springframework.http.ResponseEntity;

import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RequiredArgsConstructor
@RepositoryRestController
public class BoardRepositoryRestController {

  private final BoardRepository boardRepository;

  // spring boot data rest 에서 기본적으로 제공해주는 URL 형식을 오버라이드 함
  // @GetMapping("/boards")
  public ResponseEntity<PagedModel<Board>> getBoards(
    @PageableDefault(sort = {"idx"}, direction = DESC) Pageable pageable) {

    final Page<Board> boards = boardRepository.findAll(pageable);

    final PageMetadata pageMetadata = new PageMetadata(pageable.getPageSize(), boards.getNumber(),
      boards.getTotalElements());

    final PagedModel<Board> pagedModel = PagedModel.of(boards.getContent(), pageMetadata);
    pagedModel.add(linkTo(methodOn(BoardRepositoryRestController.class).getBoards(pageable)).withSelfRel());

    return ResponseEntity.ok(pagedModel);
  }

}
