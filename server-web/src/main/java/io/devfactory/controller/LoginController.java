package io.devfactory.controller;


import static io.devfactory.utils.FunctionUtils.redirect;

import io.devfactory.common.annotation.SocialMember;
import io.devfactory.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class LoginController {

  @GetMapping("/sign-in/form")
  public String login() {
    return "views/sign/signIn";
  }

  // oauth2 로그인 성공 시
  @GetMapping("/loginSuccess")
  public String loginSuccess(@SocialMember Member member) {
    log.debug("[dev] loginSuccess...");
    log.debug("[dev] member: {}, {}, {}, {}, {}, {}", member.getIdx(), member.getEmail(),
      member.getName(), member.getPassword(), member.getPrincipal(), member.getSocialType());

    return redirect.apply("/boards");
  }

  // oauth2 로그인 실패 시
  @GetMapping("/loginFailure")
  public String loginFailure() {
    log.debug("[dev] loginFailure...");
    return redirect.apply("/sign-in/form");
  }

}
