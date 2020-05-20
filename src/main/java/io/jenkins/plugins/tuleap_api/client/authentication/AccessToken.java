package io.jenkins.plugins.tuleap_api.client.authentication;

public interface AccessToken {

    String getAccessToken();

    String getTokenType();

    String getExpiresIn();

    String getIdToken();
}
