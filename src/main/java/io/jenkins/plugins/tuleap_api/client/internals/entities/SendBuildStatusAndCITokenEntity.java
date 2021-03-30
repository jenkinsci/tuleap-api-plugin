package io.jenkins.plugins.tuleap_api.client.internals.entities;

import com.fasterxml.jackson.annotation.JsonGetter;

public class SendBuildStatusAndCITokenEntity {
    private final String status;
    private final String token;

    public SendBuildStatusAndCITokenEntity(final String status, final String token) {
        this.status = status;
        this.token = token;
    }

    @JsonGetter("state")
    public String getStatus() {
        return status;
    }

    @JsonGetter("token")
    public String getToken() {
        return token;
    }
}
