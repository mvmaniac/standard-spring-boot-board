package io.devfactory.controller;


import static io.devfactory.common.util.FunctionUtils.redirect;

import io.devfactory.common.annotation.SocialMember;
import io.devfactory.domain.Member;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

  @GetMapping("/sign-in/form")
  public String login() {
    return "views/sign/signIn";
  }

  @GetMapping(value = "/{google|kakao}/complete")
  public String loginComplete(@SocialMember Member member) {
    return redirect.apply("/boards");
  }

}
