package io.jenkins.plugins.tuleap_api.client.internals.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.jenkins.plugins.tuleap_api.client.GitRepository;

final public class GitRepositoryEntity implements GitRepository {

    private final String name;
    private final Integer id;
    private final String path;

    public GitRepositoryEntity(@JsonProperty("name") String name,
                               @JsonProperty("id") Integer id,
                               @JsonProperty("path") String path)
    {
        this.name = name;
        this.id = id;
        this.path = path;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Integer getId() {
        return this.id;
    }

    @Override
    public String getPath() {
        return this.path;
    }
}
