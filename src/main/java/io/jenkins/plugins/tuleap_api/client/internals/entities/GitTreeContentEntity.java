package io.jenkins.plugins.tuleap_api.client.internals.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.jenkins.plugins.tuleap_api.client.GitTreeContent;

public class GitTreeContentEntity implements GitTreeContent {

    private final String id;
    private final String name;
    private final String path;
    private final String type;
    private final String mode;


    public GitTreeContentEntity(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("path") String path,
        @JsonProperty("type") String type,
        @JsonProperty("mode") String mode
    ) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.type = type;
        this.mode = mode;
    }


    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getMode() {
        return mode;
    }
}
