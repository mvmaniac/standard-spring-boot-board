package io.devfactory.common.resolver;

import static io.devfactory.domain.enums.SocialType.GOOGLE;
import static io.devfactory.domain.enums.SocialType.KAKAO;

import io.devfactory.common.annotation.SocialMember;
import io.devfactory.domain.Member;
import io.devfactory.domain.enums.SocialType;
import io.devfactory.repository.MemberRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Slf4j
@RequiredArgsConstructor
@Component
public class MemberArgumentResolver implements HandlerMethodArgumentResolver {

  private final MemberRepository memberRepository;

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return Objects.nonNull(parameter.getParameterAnnotation(SocialMember.class)) && parameter
      .getParameterType().equals(Member.class);
  }

  @Override
  public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
    NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

    final HttpSession session = ((ServletRequestAttributes) RequestContextHolder
      .currentRequestAttributes()).getRequest().getSession();
    final Member member = (Member) session.getAttribute("member");

    return getMember(member, session);
  }

  private Member getMember(Member member, HttpSession session) {
    try {
      if (Objects.isNull(member)) {
        final OAuth2Authentication authentication = (OAuth2Authentication) SecurityContextHolder
          .getContext().getAuthentication();

        final HashMap<String, String> map = (HashMap<String, String>) authentication.getDetails();
        Member convertMember = convertMember(
          String.valueOf(authentication.getAuthorities().toArray()[0]), map);

        if (Objects.isNull(convertMember)) {
          // TODO: 에러 던지기
          return convertMember;
        }

        final Member findMember = memberRepository.findMemberByEmail(convertMember.getEmail())
          .orElseGet(() -> {
            log.debug("[dev] member save...");
            return memberRepository.save(convertMember);
          });

        setRoleIfNotSame(findMember, authentication, map);
        session.setAttribute("member", findMember);
      }
    } catch (ClassCastException e) {
      return member;
    }

    return member;
  }

  private Member convertMember(String authority, HashMap<String, String> map) {
    if (GOOGLE.isEquals(authority)) {
      return getModernMember(GOOGLE, map);
    } else if (KAKAO.isEquals(authority)) {
      return getKakaoMember(map);
    }

    return null;
  }

  private Member getModernMember(SocialType socialType, Map<String, String> map) {
    return Member.create()
      .name(map.get("name"))
      .email(map.get("email"))
      .principal(map.get("id"))
      .socialType(socialType)
      .build();
  }

  private Member getKakaoMember(Map<String, String> map) {
    final Map<String, String> properties = (HashMap<String, String>) (Object) map.get("properties");

    return Member.create()
      .name(properties.get("nickname"))
      .email(map.get("kaccount_email"))
      .principal(map.get("id"))
      .socialType(KAKAO)
      .build();
  }

  private void setRoleIfNotSame(Member member, OAuth2Authentication authentication,
    Map<String, String> map) {

    final String roleType = member.getSocialType().getRoleType();
    final SimpleGrantedAuthority authority = new SimpleGrantedAuthority(roleType);

    if (authentication.getAuthorities().contains(authority)) {
      SecurityContextHolder.getContext()
        .setAuthentication(new UsernamePasswordAuthenticationToken(map, "N/A",
          AuthorityUtils.createAuthorityList(roleType)));
    }
  }

}
