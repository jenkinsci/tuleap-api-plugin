package io.jenkins.plugins.tuleap_api.client.authentication;

public interface TokenResponse extends AccessToken {
    String getIdToken();
}
