package io.devfactory.config;

import io.devfactory.oauth.CustomOAuth2Provider;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.StaticResourceLocation;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.Arrays;
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
      .securityMatcher(StaticResource.getResources())
      .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
      .requestCache(RequestCacheConfigurer::disable)
      .securityContext(AbstractHttpConfigurer::disable)
      .sessionManagement(AbstractHttpConfigurer::disable)
    .build();
    // @formatter:on
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http,
      HandlerMappingIntrospector introspector) throws Exception {
    final var mvcMatcher = new MvcRequestMatcher.Builder(introspector);

    // @formatter:off
    return http
      .authorizeHttpRequests(authorize -> authorize
        .requestMatchers(
            mvcMatcher.pattern("/")
            , mvcMatcher.pattern("/oauth2/**")
            , mvcMatcher.pattern("/login/**")
            , mvcMatcher.pattern("/sign-up/form")
            , mvcMatcher.pattern("/sign-in/form")
        ).permitAll()
        .requestMatchers(mvcMatcher.pattern("/google"))
          .hasAnyAuthority(GOOGLE.getRoleType())
        .requestMatchers(mvcMatcher.pattern("/kakao"))
          .hasAnyAuthority(KAKAO.getRoleType())
        .anyRequest()
          .authenticated())
      .oauth2Login(oauth2 -> oauth2
        .defaultSuccessUrl("/loginSuccess")
        .failureUrl("/loginFailure"))
      .headers(headers -> headers
        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
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

    // @formatter:off
    final var registrations = oAuth2ClientProperties.getRegistration()
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
    // @formatter:on

    return new InMemoryClientRegistrationRepository(registrations);
  }

  private ClientRegistration getRegistration(OAuth2ClientProperties clientProperties,
      String client) {

    if ("google".equals(client)) {
      final var registration = clientProperties.getRegistration().get("google");
      // @formatter:off
      return CommonOAuth2Provider.GOOGLE.getBuilder(client)
        .clientId(registration.getClientId())
        .clientSecret(registration.getClientSecret())
        .scope("email", "profile")
        .build();
      // @formatter:on
    }

    return null;
  }

  @Getter
  public static class StaticResource {

    private StaticResource() {
      throw new IllegalStateException("Constructor not supported");
    }

    private static final String[] DEFAULT_RESOURCES = Arrays.stream(StaticResourceLocation.values())
        .flatMap(StaticResourceLocation::getPatterns)
        .toArray(String[]::new);

    public static String[] getResources() {
      return DEFAULT_RESOURCES;
    }

    public static String[] getResources(String... antPatterns) {
      final var defaultResources = StaticResource.DEFAULT_RESOURCES;
      final var resources = Arrays.copyOf(defaultResources, defaultResources.length + antPatterns.length);
      System.arraycopy(antPatterns, 0, resources, defaultResources.length, antPatterns.length);
      return resources;
    }

  }

}
