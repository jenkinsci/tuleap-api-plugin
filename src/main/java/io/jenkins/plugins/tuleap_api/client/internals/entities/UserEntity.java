package io.jenkins.plugins.tuleap_api.client.internals.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.jenkins.plugins.tuleap_api.client.User;

public class UserEntity implements User {
    private String username;

    public UserEntity(@JsonProperty("username") String username) {
        this.username = username;
    }

    @Override
    public String getUsername() {
        return username;
    }
}
