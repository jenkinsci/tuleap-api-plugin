package io.jenkins.plugins.tuleap_api.client.internals.entities;

import com.fasterxml.jackson.annotation.JsonGetter;

public class SendBuildStatusEntity {
    private final String status;

    public SendBuildStatusEntity(final String status) {
        this.status = status;
    }

    @JsonGetter("state")
    public String getStatus() {
        return status;
    }
}
