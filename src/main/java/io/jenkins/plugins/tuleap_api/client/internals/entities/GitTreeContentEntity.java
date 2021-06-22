package io.jenkins.plugins.tuleap_api.client.internals.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.jenkins.plugins.tuleap_api.client.GitTreeContent;

public class GitTreeContentEntity implements GitTreeContent {

    private final String id;
    private final String name;
    private final String path;
    private final ContentType type;
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
        this.mode = mode;
        this.type = getComputedType(type);
    }

    private ContentType getComputedType(String type) {
        if ("tree".equals(type)) {
            return ContentType.TREE;
        }

        if ("120000".equals(this.mode)) {
            return ContentType.SYMLINK;
        }

        return ContentType.BLOB;
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
    public ContentType getType() {
        return type;
    }

    @Override
    public String getMode() {
        return mode;
    }
}
