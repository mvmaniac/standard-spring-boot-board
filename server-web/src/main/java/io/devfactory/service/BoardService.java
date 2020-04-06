package io.devfactory.service;

import io.devfactory.domain.Board;
import io.devfactory.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static io.devfactory.util.FunctionUtils.emptyEntity;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BoardService {

    private final BoardRepository boardRepository;

    public Page<Board> findBoards(Pageable pageable) {
        final PageRequest pageRequest = PageRequest.of(
            pageable.getPageNumber() <= 0 ? 0 : pageable.getPageNumber() - 1,
            pageable.getPageSize(),
            pageable.getSort()
        );
        return boardRepository.findAll(pageRequest);
    }

    public Board findBoardByIdx(Long idx) {
        return boardRepository.findById(idx).orElseGet(emptyEntity(Board.create().build()));
    }

}
