package io.devfactory.controller;

import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import io.devfactory.domain.Board;
import io.devfactory.repository.BoardRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.PagedModel.PageMetadata;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    final PagedModel<Board> pagedModel = PagedModel.of(boards.getContent(), pageMetadata);
    pagedModel.add(linkTo(methodOn(BoardRestController.class).getBoards(pageable)).withSelfRel());

    return ResponseEntity.ok(pagedModel);
  }

  @PostMapping
  public ResponseEntity<Long> postBoard(@RequestBody Board board) {
    final Board savedBoard = boardRepository.save(board);
    return new ResponseEntity<>(savedBoard.getIdx(), HttpStatus.CREATED);
  }

  @PutMapping("/{idx}")
  public ResponseEntity<Boolean> putBoard(@PathVariable("idx") Long idx, @RequestBody Board board) {
    final Board findBoard = boardRepository.findById(idx).orElseThrow(EntityNotFoundException::new);
    findBoard.update(board);
    boardRepository.save(findBoard);

    return ResponseEntity.ok(true);
  }

  @DeleteMapping("/{idx}")
  public ResponseEntity<Boolean> deleteBoard(@PathVariable("idx") Long idx) {
    final Board findBoard = boardRepository.findById(idx).orElseThrow(EntityNotFoundException::new);
    boardRepository.deleteById(findBoard.getIdx());

    return ResponseEntity.ok(true);
  }

}
