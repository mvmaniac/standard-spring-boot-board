package io.devfactory.domain.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum BoardType {

  @JsonProperty("NOTICE")
  NOTICE("공지사항"),

  @JsonProperty("FREE")
  FREE("자유게시판");

  private String value;

  BoardType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

}
