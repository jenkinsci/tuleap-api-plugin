package io.jenkins.plugins.tuleap_api.client.internals.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.jenkins.plugins.tuleap_api.client.Project;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectEntity implements Project {

    private String shortname;
    private Integer id;

    public ProjectEntity(
        @JsonProperty("shortname") String shortname,
        @JsonProperty("id") Integer id
    ){
        this.shortname = shortname;
        this.id = id;
    }

    public String getShortname() {
        return this.shortname;
    }

    public Integer getId() {
        return this.id;
    }
}
