package io.jenkins.plugins.tuleap_api.client.internals.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.jenkins.plugins.tuleap_api.client.Project;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectEntity{

    private String shortname;

    public ProjectEntity(@JsonProperty("shortname") String shortname){
        this.shortname = shortname;
    }

    public String getShortname() {
        return this.shortname;
    }
}
