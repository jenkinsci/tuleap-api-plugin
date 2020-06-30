package io.jenkins.plugins.tuleap_api.client.authentication;

public interface UserInfo {
    String getSubject();
    String getUsername();
    String getName();
    String getEmail();
    boolean isEmailVerified();
}
