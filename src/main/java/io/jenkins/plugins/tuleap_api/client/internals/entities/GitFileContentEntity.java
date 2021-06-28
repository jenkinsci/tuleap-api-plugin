package io.jenkins.plugins.tuleap_api.client.internals.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.jenkins.plugins.tuleap_api.client.GitFileContent;

public class GitFileContentEntity implements GitFileContent {

    private final String encoding;
    private final Integer size;
    private final String name;
    private final String path;
    private final String content;


    public GitFileContentEntity(
        @JsonProperty("encoding") String encoding,
        @JsonProperty("size") Integer size,
        @JsonProperty("name") String name,
        @JsonProperty("path") String path,
        @JsonProperty("content") String content
    ) {
        this.encoding = encoding;
        this.size = size;
        this.name = name;
        this.content = content;
        this.path = path;
    }

    @Override
    public String getEncoding() {
        return this.encoding;
    }

    @Override
    public Integer getSize() {
        return this.size;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public String getContent() {
        return this.content;
    }
}
