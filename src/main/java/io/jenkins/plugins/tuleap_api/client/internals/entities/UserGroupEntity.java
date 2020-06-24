package io.jenkins.plugins.tuleap_api.client.internals.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.jenkins.plugins.tuleap_api.client.UserGroup;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserGroupEntity implements UserGroup {

    private String shortName;
    private ProjectEntity project;

    public UserGroupEntity(@JsonProperty("short_name") String shortName, @JsonProperty("project") ProjectEntity project) {
        this.shortName = shortName;
        this.project = project;
    }

    @Override
    public String getGroupName() {
        return this.shortName;
    }

    @Override
    public String getProjectName() {
        return this.project.getShortname();
    }
}
