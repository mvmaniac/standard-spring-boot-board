package io.devfactory.domain.enums;

public enum SocialType {

  GOOGLE("google"),
  KAKAO("kakao");

  private String name;

  SocialType(String name) {
    this.name = name;
  }

  public String getRoleType() {
    return "ROLE_" + name.toUpperCase();
  }

  public String getValue() {
    return name;
  }

  public boolean isEquals(String authority) {
    return getRoleType().equals(authority);
  }

}
