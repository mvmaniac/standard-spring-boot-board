package io.devfactory.config;

import static io.devfactory.domain.enums.SocialType.GOOGLE;
import static io.devfactory.domain.enums.SocialType.KAKAO;

import io.devfactory.domain.enums.SocialType;
import io.devfactory.oauth.ClientResources;
import io.devfactory.oauth.UserTokenService;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.CompositeFilter;

@RequiredArgsConstructor
@EnableOAuth2Client
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  private final OAuth2ClientContext oAuth2ClientContext;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    final CharacterEncodingFilter filter = new CharacterEncodingFilter();

    // @formatter:off
    http
      .authorizeRequests()
        .antMatchers("/", "/login/**")
          .permitAll()
      .anyRequest()
        .authenticated()
        .and()
      .headers()
        .frameOptions()
          .disable()
        .and()
      .exceptionHandling()
        .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
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
      .addFilterBefore(oauth2Filter(), BasicAuthenticationFilter.class)
      .csrf()
        .disable();
    // @formatter:on
  }

  @Override
  public void configure(WebSecurity web) {
    web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
  }

  @Bean
  public FilterRegistrationBean<Filter> oauth2ClientFilterRegistration(
    OAuth2ClientContextFilter filter) {
    final FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();

    registrationBean.setFilter(filter);
    registrationBean.setOrder(-100);

    return registrationBean;
  }

  @Bean
  @ConfigurationProperties("google")
  public ClientResources google() {
    return new ClientResources();
  }

  @Bean
  @ConfigurationProperties("kakao")
  public ClientResources kakao() {
    return new ClientResources();
  }

  private Filter oauth2Filter() {
    final CompositeFilter filter = new CompositeFilter();

    final List<Filter> filters = new ArrayList<>();
    filters.add(oauth2Filter(google(), "/login/google", GOOGLE));
    filters.add(oauth2Filter(kakao(), "/login/kakao", KAKAO));

    filter.setFilters(filters);
    return filter;
  }

  private Filter oauth2Filter(ClientResources client, String path, SocialType socialType) {
    final OAuth2ClientAuthenticationProcessingFilter filter =
      new OAuth2ClientAuthenticationProcessingFilter(path);
    final OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(client.getClient(),
      oAuth2ClientContext);

    filter.setRestTemplate(restTemplate);
    filter.setTokenServices(new UserTokenService(client, socialType));
    filter.setAuthenticationSuccessHandler((request, response, authentication) -> response
      .sendRedirect("/" + socialType.getValue() + "/complete"));
    filter.setAuthenticationFailureHandler(
      (request, response, exception) -> response.sendRedirect("/error"));

    return filter;
  }

}
