package io.devfactory.controller;

import io.devfactory.domain.Board;
import io.devfactory.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RequiredArgsConstructor
@Controller
public class BoardController {

  private final BoardService boardService;

  @GetMapping("/boards")
  public String boardList(@PageableDefault(sort = {"idx"}, direction = DESC) Pageable pageable, Model model) {
    final Page<Board> boards = boardService.findBoards(pageable);

    model.addAttribute("count", boards.getTotalElements());
    model.addAttribute("boards", boards);

    return "views/board/list";
  }

  @GetMapping("/boards/form")
  public String boardForm(@RequestParam(defaultValue = "0") Long idx, Model model) {
    model.addAttribute("board", boardService.findBoardByIdx(idx));
    return "views/board/form";
  }

  // TODO: 추후 확인 후 제거
  @GetMapping("/boards/{idx}")
  public String boardView(@PathVariable("idx") Long idx, Model model) {
    final Board findBoard = boardService.findBoardByIdx(idx);
    model.addAttribute("board", findBoard);
    return "views/board/detail";
  }

}
