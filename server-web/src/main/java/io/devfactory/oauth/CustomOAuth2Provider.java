package io.devfactory.oauth;

import static org.springframework.security.oauth2.core.AuthorizationGrantType.AUTHORIZATION_CODE;
import static org.springframework.security.oauth2.core.ClientAuthenticationMethod.POST;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistration.Builder;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

public enum CustomOAuth2Provider {

  KAKAO {
    @Override
    public Builder getBuilder(String registrationId) {
      final Builder builder = getBuilder(registrationId, POST, DEFAULT_LOGIN_REDIRECT_URL);
      builder.scope("profile", "account_email");
      builder.authorizationUri("https://kauth.kakao.com/oauth/authorize");
      builder.tokenUri("https://kauth.kakao.com/oauth/token");
      builder.userInfoUri("https://kapi.kakao.com/v2/user/me");
      builder.userNameAttributeName("id");
      builder.clientName("kakao");
      return builder;
    }
  };


  private static final String DEFAULT_LOGIN_REDIRECT_URL = "{baseUrl}/login/oauth2/code/{registrationId}";

  protected final ClientRegistration.Builder getBuilder(String registrationId,
    ClientAuthenticationMethod method, String redirectUri) {
    final Builder builder = ClientRegistration.withRegistrationId(registrationId);
    builder.clientAuthenticationMethod(method);
    builder.authorizationGrantType(AUTHORIZATION_CODE);
    builder.redirectUriTemplate(redirectUri);
    return builder;
  }

  public abstract ClientRegistration.Builder getBuilder(String registrationId);

}
