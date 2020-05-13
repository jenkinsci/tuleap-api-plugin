package io.jenkins.plugins.tuleap_api.client.internals.entities.authentication;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.jenkins.plugins.tuleap_api.client.authentication.UserInfo;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInfoEntity implements UserInfo {

    private final String subject;
    private final String username;

    public UserInfoEntity(
        @JsonProperty("sub") String subject,
        @JsonProperty("preferred_username") String username
    ) {
        this.subject = subject;
        this.username = username;
    }
    @Override
    public String getSubject() {
        return this.subject;
    }

    @Override
    public String getUsername() {
        return this.username;
    }
}
