package io.jenkins.plugins.tuleap_api.client.internals.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AccessKeyEntity {
    private List<AccessKeyScopeEntity> scopes;

    public AccessKeyEntity(
        @JsonProperty("scopes") List<AccessKeyScopeEntity> scopes
    ) {
        this.scopes = scopes;
    }

    public List<AccessKeyScopeEntity> getScopes() {
        return Optional.ofNullable(scopes).orElse(new ArrayList<>());
    }
}
