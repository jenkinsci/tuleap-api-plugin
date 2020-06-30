package io.jenkins.plugins.tuleap_api.client.internals.entities.authentication;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.jenkins.plugins.tuleap_api.client.authentication.UserInfo;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInfoEntity implements UserInfo {

    private final String subject;
    private final String username;
    private final String name;
    private final String email;
    private final boolean emailVerified;

    public UserInfoEntity(
        @JsonProperty("sub") String subject,
        @JsonProperty("preferred_username") String username,
        @JsonProperty("name") String name,
        @JsonProperty("email") String email,
        @JsonProperty("email_verified") boolean emailVerified
    ) {
        this.subject = subject;
        this.username = username;
        this.name = name;
        this.email = email;
        this.emailVerified = emailVerified;
    }
    @Override
    public String getSubject() {
        return this.subject;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getEmail() {
        return this.email;
    }

    @Override
    public boolean isEmailVerified() {
        return this.emailVerified;
    }
}
