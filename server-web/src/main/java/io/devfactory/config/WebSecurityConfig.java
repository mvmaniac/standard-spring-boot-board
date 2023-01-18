package io.devfactory.config;

import io.devfactory.oauth.CustomOAuth2Provider;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.StaticResourceLocation;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties.Registration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static io.devfactory.domain.enums.SocialType.GOOGLE;
import static io.devfactory.domain.enums.SocialType.KAKAO;
import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@Configuration
public class WebSecurityConfig {

  @Order(0)
  @Bean
  public SecurityFilterChain resources(HttpSecurity http) throws Exception {
    // @formatter:off
    return http
      .securityMatchers(matcher -> matcher.requestMatchers(StaticResource.getResources()))
      .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
      .requestCache(RequestCacheConfigurer::disable)
      .securityContext(AbstractHttpConfigurer::disable)
      .sessionManagement(AbstractHttpConfigurer::disable)
    .build();
    // @formatter:on
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    // @formatter:off
    return http
      .authorizeHttpRequests(authorize -> authorize
        .requestMatchers("/", "/oauth2/**", "/login/**", "/sign-up/form", "/sign-in/form")
          .permitAll()
        .requestMatchers("/google")
          .hasAnyAuthority(GOOGLE.getRoleType())
        .requestMatchers("/kakao")
          .hasAnyAuthority(KAKAO.getRoleType())
        .anyRequest()
          .authenticated())
      .oauth2Login(oauth2 -> oauth2
        .defaultSuccessUrl("/loginSuccess")
        .failureUrl("/loginFailure"))
      .headers(headers -> headers.frameOptions().disable())
      .exceptionHandling(exception -> exception
        .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/sign-in/form")))
      .formLogin(form -> form.successForwardUrl("/boards"))
      .logout(logout -> logout
        .logoutUrl("/logout")
        .logoutSuccessUrl("/")
        .deleteCookies("JSESSIONID")
        .invalidateHttpSession(true))
      .csrf(AbstractHttpConfigurer::disable)
      .addFilterBefore(new CharacterEncodingFilter(), CsrfFilter.class)
      .build();
    // @formatter:on
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
      .clientSecret("test") // 필요 없는 값 이지만 null 이면 실행이 안 되므로 임시 값을 넣음
      .jwkSetUri("test") // 필요 없는 값 이지만 null 이면 실행이 안 되므로 임시 값을 넣음
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

  @Getter
  public static class StaticResource {

    private StaticResource() {
      throw new IllegalStateException("Constructor not supported");
    }

    private static final String[] defaultResources = Arrays.stream(StaticResourceLocation.values())
        .flatMap(StaticResourceLocation::getPatterns)
        .toArray(String[]::new);

    public static String[] getResources() {
      return defaultResources;
    }

    public static String[] getResources(String... antPatterns) {
      final var defaultResources = StaticResource.defaultResources;
      final var resources = Arrays.copyOf(defaultResources, defaultResources.length + antPatterns.length);
      System.arraycopy(antPatterns, 0, resources, defaultResources.length, antPatterns.length);
      return resources;
    }

  }

}
