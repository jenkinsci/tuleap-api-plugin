package io.jenkins.plugins.tuleap_api.client.internals.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.jenkins.plugins.tuleap_api.client.GitRepositoryReference;

public class GitRepositoryReferenceEntity implements GitRepositoryReference {

    private final String name;
    private final Integer id;

    public GitRepositoryReferenceEntity(@JsonProperty("name") String name, @JsonProperty("id") Integer id){
        this.name = name;
        this.id = id;
    }
    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Integer getId() {
        return this.id;
    }
}
