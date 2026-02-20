package io.devfactory.view.mapper;

import io.devfactory.view.config.MyBatisConfig;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@Import(MyBatisConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@MybatisTest
class ArticleViewCountBackUpMapperTest {

  @Autowired
  private ArticleViewCountBackUpMapper articleViewCountBackUpMapper;

  @Transactional
  @Test
  void updateViewCountTest() {
    // given
    articleViewCountBackUpMapper.upsertArticleViewCount(1L, 0L);

    // when
    final var result1 = articleViewCountBackUpMapper.upsertArticleViewCount(1L, 100L);
    final var result2 = articleViewCountBackUpMapper.upsertArticleViewCount(1L, 300L);
    final var result3 = articleViewCountBackUpMapper.upsertArticleViewCount(1L, 200L);

    // then
    // mysql에서는 upsert(INSERT ... ON DUPLICATE KEY UPDATE)의 반환 값은 아래와 같음 (mariadb도 같음, postgresql의 경우에는 1로 반환됨)
    // 새로 넣으면: 1 (신규 삽입)
    // 기존 데이터가 있고 값이 바뀌면: 2 (찾음 1 + 바뀜 1)
    // 기존 데이터가 있고 값도 같으면: 1 (찾음 1 + 바뀜 0)
    assertThat(result1).isEqualTo(2);
    assertThat(result2).isEqualTo(2);
    assertThat(result3).isEqualTo(1);

    final var articleViewCount = articleViewCountBackUpMapper.selectArticleViewCountById(1L);
    assertThat(articleViewCount).isEqualTo(300L);
  }

}
