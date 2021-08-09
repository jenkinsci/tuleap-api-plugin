package io.jenkins.plugins.tuleap_api.client.internals.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.jenkins.plugins.tuleap_api.client.Project;

public class ProjectEntity implements Project {

    private String shortname;
    private Integer id;
    private final String uri;
    private final String label;

    public ProjectEntity(
        @JsonProperty("shortname") String shortname,
        @JsonProperty("id") Integer id,
        @JsonProperty("uri") String uri,
        @JsonProperty("label") String label
    ) {
        this.shortname = shortname;
        this.id = id;
        this.uri = uri;
        this.label = label;
    }

    public String getShortname() {
        return this.shortname;
    }

    @Override
    public String getUri() {
        return this.uri;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

    public Integer getId() {
        return this.id;
    }
}
