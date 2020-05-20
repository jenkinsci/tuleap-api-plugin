package io.jenkins.plugins.tuleap_api.client.authentication;

import com.auth0.jwk.Jwk;

import java.util.List;

public interface OpenIDClientApi {
    String DISCOVERY_API = "/.well-known/openid-configuration";
    String USER_INFO_API = "/oauth2/userinfo";

    List<Jwk> getSigningKeys();

    UserInfo getUserInfo(AccessToken accessToken);

    String getProviderIssuer();
}
