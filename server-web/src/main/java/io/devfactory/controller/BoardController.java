package io.devfactory.controller;

import io.devfactory.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
public class BoardController {

    private final BoardService boardService;

    @GetMapping({"", "/"})
    public String boardForm(@RequestParam(defaultValue = "0") Long idx, Model model) {
        model.addAttribute("board", boardService.findBoardByIdx(idx));
        return "board/form";
    }

    @GetMapping("/list")
    public String boardList(@PageableDefault Pageable pageable, Model model) {
        model.addAttribute("boardList", boardService.findBoards(pageable));
        return "board/list";
    }

}
