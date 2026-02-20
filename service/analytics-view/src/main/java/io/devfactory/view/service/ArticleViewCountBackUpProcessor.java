package io.devfactory.view.service;

import io.devfactory.view.mapper.ArticleViewCountBackUpMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class ArticleViewCountBackUpProcessor {

  private final ArticleViewCountBackUpMapper articleViewCountBackUpMapper;

  @Transactional
  public void backUp(Long articleId, Long viewCount) {
    articleViewCountBackUpMapper.upsertArticleViewCount(articleId, viewCount);
  }

}
