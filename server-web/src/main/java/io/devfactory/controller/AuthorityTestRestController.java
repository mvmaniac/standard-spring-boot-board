package io.devfactory.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthorityTestRestController {

  @GetMapping("/google")
  public String google() {
    return "google";
  }

  @GetMapping("/kakao")
  public String kakao() {
    return "kakao";
  }

}
