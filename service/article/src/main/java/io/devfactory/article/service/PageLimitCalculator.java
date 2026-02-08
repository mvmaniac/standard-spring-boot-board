package io.devfactory.article.service;

public final class PageLimitCalculator {

  private PageLimitCalculator() {
    // nothing
  }

  public static Long calculatePageLimit(Long page, Long pageSize, Long movablePageCount) {
    return (((page - 1) / movablePageCount) + 1) * pageSize * movablePageCount + 1;
  }

}
