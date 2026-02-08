package io.devfactory.article.service;

import io.devfactory.article.dto.request.ArticleCreateRequest;
import io.devfactory.article.dto.request.ArticleUpdateRequest;
import io.devfactory.article.dto.response.ArticlePageResponse;
import io.devfactory.article.dto.response.ArticleResponse;
import io.devfactory.article.entity.Article;
import io.devfactory.article.entity.BoardArticleCount;
import io.devfactory.article.mapper.ArticleMapper;
import io.devfactory.article.mapper.BoardArticleCountMapper;
import io.devfactory.article.repository.ArticleRepository;
import io.devfactory.article.repository.BoardArticleCountRepository;
import io.devfactory.common.Snowflake;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.PageFormat;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ArticleService {

  private final ArticleRepository articleRepository;
  private final ArticleMapper articleMapper;

  private final BoardArticleCountRepository boardArticleCountRepository;
  private final BoardArticleCountMapper boardArticleCountMapper;

  private final Snowflake snowflake = new Snowflake();

  public ArticlePageResponse readWithPaging(Long boardId, Long page, Long pageSize) {
    final var articles = articleMapper.findArticlesWithPaging(boardId, (page - 1) * pageSize, pageSize).stream()
        .map(ArticleResponse::from)
        .toList();
    final var articleCount = articleMapper.countArticlesWithLimit(boardId,
        PageLimitCalculator.calculatePageLimit(page, pageSize, 10L));
    return ArticlePageResponse.of(articles, articleCount);
  }

  public List<ArticleResponse> readWithScroll(Long boardId, Long pageSize, Long lastArticleId) {
    final var articles = lastArticleId == null
        ? articleMapper.findArticlesWithScroll(boardId, pageSize)
        : articleMapper.findArticlesNextWithScroll(boardId, pageSize, lastArticleId);
    return articles.stream().map(ArticleResponse::from).toList();
  }

  public ArticleResponse read(Long articleId) {
    return ArticleResponse.from(articleRepository.findById(articleId).orElseThrow());
  }

  @Transactional
  public ArticleResponse create(ArticleCreateRequest request) {
    final var newArticle = Article.create(snowflake.nextId(), request.getTitle(), request.getContent(), request.getBoardId(), request.getWriterId());
    final var savedArticle = articleRepository.save(newArticle);
    articleRepository.flush(); // 명시적 호출

    int result = boardArticleCountMapper.incrementBoardArticleCount(savedArticle.getBoardId());
    if (result == 0) {
      boardArticleCountMapper.initBoardArticleCount(savedArticle.getBoardId());
    }

    return ArticleResponse.from(savedArticle);
  }

  @Transactional
  public ArticleResponse update(Long articleId, ArticleUpdateRequest request) {
    final var target = articleRepository.findById(articleId).orElseThrow();
    target.update(request.getTitle(), request.getContent());
    return ArticleResponse.from(target);
  }

  @Transactional
  public void delete(Long articleId) {
    final var target = articleRepository.findById(articleId).orElseThrow();
    articleRepository.delete(target);
    articleRepository.flush(); // 명시적 호출

    boardArticleCountMapper.decreaseBoardArticleCount(target.getBoardId());
  }

  public Long countArticles(Long boardId) {
    return boardArticleCountRepository.findById(boardId)
        .map(BoardArticleCount::getArticleCount)
        .orElse(0L);
  }

}
