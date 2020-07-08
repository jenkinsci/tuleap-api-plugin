package io.jenkins.plugins.tuleap_api.client.internals.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.jenkins.plugins.tuleap_api.client.UserGroup;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MinimalUserGroupEntity implements UserGroup {

    private String shortName;

    public MinimalUserGroupEntity(@JsonProperty("short_name") String shortName) {
        this.shortName = shortName;
    }

    @Override
    public String getGroupName() {
        return this.shortName;
    }

    @Override
    public String getProjectName() {
        return null;
    }
}
