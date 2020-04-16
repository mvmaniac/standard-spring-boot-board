package io.devfactory.controller;

import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import io.devfactory.domain.Board;
import io.devfactory.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.PagedModel.PageMetadata;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/boards")
@RestController
public class BoardRestController {

  private final BoardRepository boardRepository;

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PagedModel<Board>> getBoards(
    @PageableDefault(sort = {"idx"}, direction = DESC) Pageable pageable) {

    final Page<Board> boards = boardRepository.findAll(pageable);

    final PageMetadata pageMetadata = new PageMetadata(pageable.getPageSize(), boards.getNumber(),
      boards.getTotalElements());

    final PagedModel<Board> pagedModel = new PagedModel<>(boards.getContent(), pageMetadata);
    pagedModel.add(linkTo(methodOn(BoardRestController.class).getBoards(pageable)).withSelfRel());

    return ResponseEntity.ok(pagedModel);
  }

}
