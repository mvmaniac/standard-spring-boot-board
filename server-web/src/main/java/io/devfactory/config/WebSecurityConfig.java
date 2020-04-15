package io.devfactory.config;

import static io.devfactory.domain.enums.SocialType.GOOGLE;
import static io.devfactory.domain.enums.SocialType.KAKAO;
import static java.util.stream.Collectors.toList;

import io.devfactory.oauth.CustomOAuth2Provider;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties.Registration;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.CharacterEncodingFilter;

@RequiredArgsConstructor
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    final CharacterEncodingFilter filter = new CharacterEncodingFilter();

    // @formatter:off
    http
      .authorizeRequests()
        .antMatchers("/", "/oauth2/**", "/login/**", "/sign-up/form", "/sign-in/form")
          .permitAll()
        .antMatchers("/google")
          .hasAnyAuthority(GOOGLE.getRoleType())
        .antMatchers("/kakao")
          .hasAnyAuthority(KAKAO.getRoleType())
        .anyRequest()
          .authenticated()
        .and()
      .oauth2Login()
        .defaultSuccessUrl("/loginSuccess")
        .failureUrl("/loginFailure")
        .and()
      .headers()
        .frameOptions()
          .disable()
        .and()
      .exceptionHandling()
        .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/sign-in/form"))
        .and()
      .formLogin()
        .successForwardUrl("/boards")
        .and()
      .logout()
        .logoutUrl("/logout")
        .logoutSuccessUrl("/")
        .deleteCookies("JSESSIONID")
        .invalidateHttpSession(true)
        .and()
      .addFilterBefore(filter, CsrfFilter.class)
      .csrf()
        .disable();
    // @formatter:on
  }

  @Override
  public void configure(WebSecurity web) {
    web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
  }

  @Bean
  public ClientRegistrationRepository clientRegistrationRepository(
    OAuth2ClientProperties oAuth2ClientProperties,
    @Value("${custom.oauth2.kakao.client-id}") String kakaoClientId) {

    final List<ClientRegistration> registrations = oAuth2ClientProperties.getRegistration()
      .keySet()
      .stream()
      .map(client -> getRegistration(oAuth2ClientProperties, client))
      .filter(Objects::nonNull)
      .collect(toList());

    registrations.add(CustomOAuth2Provider.KAKAO.getBuilder("kakao")
      .clientId(kakaoClientId)
      .clientSecret("test") // 필요 없는 값이지만 null이면 실행이 안 되므로 임시값을 넣음
      .jwkSetUri("test") // 필요 없는 값이지만 null이면 실행이 안 되므로 임시값을 넣음
      .build());

    return new InMemoryClientRegistrationRepository(registrations);
  }

  private ClientRegistration getRegistration(OAuth2ClientProperties clientProperties,
    String client) {

    if ("google".equals(client)) {
      final Registration registration = clientProperties.getRegistration().get("google");
      return CommonOAuth2Provider.GOOGLE.getBuilder(client)
        .clientId(registration.getClientId())
        .clientSecret(registration.getClientSecret())
        .scope("email", "profile")
        .build();
    }

    return null;
  }

}
