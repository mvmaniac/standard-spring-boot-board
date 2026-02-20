package io.devfactory.article.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.context.ActiveProfiles;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
class PageLimitCalculatorTest {

  @DisplayName("calculatePageLimit")
  @ParameterizedTest
  @MethodSource("calculatePageLimitParams")
  void calculatePageLimit(Long page, Long pageSize, Long movablePageCount, Long expected) {
    Long result = PageLimitCalculator.calculatePageLimit(page, pageSize, movablePageCount);
    assertThat(result).isEqualTo(expected);
  }

  private static Stream<Arguments> calculatePageLimitParams() {
    return Stream.of(
      Arguments.of(1L, 30L, 10L, 301L),
      Arguments.of(7L, 30L, 10L, 301L),
      Arguments.of(10L, 30L, 10L, 301L),
      Arguments.of(11L, 30L, 10L, 601L),
      Arguments.of(12L, 30L, 10L, 601L)
    );
  }

}
