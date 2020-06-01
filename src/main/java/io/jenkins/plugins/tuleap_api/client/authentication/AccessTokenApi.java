package io.jenkins.plugins.tuleap_api.client.authentication;

import hudson.util.Secret;

public interface AccessTokenApi {
    String ACCESS_TOKEN_API = "/oauth2/token";
    String ACCESS_TOKEN_CONTENT_TYPE = "application/x-www-form-urlencoded";
    String REDIRECT_URI = "securityRealm/finishLogin";

    String REFRESH_TOKEN_SCOPES = "read:project read:user_membership openid profile offline_access";

    AccessToken getAccessToken(
        String codeVerifier,
        String authorizationCode,
        String clientId,
        Secret clientSecret
    );

    AccessToken refreshToken(AccessToken accessToken, String clientId, Secret clientSecret);
}
