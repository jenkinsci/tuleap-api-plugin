package io.jenkins.plugins.tuleap_api.client.internals.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.jenkins.plugins.tuleap_api.client.GitRepositories;
import io.jenkins.plugins.tuleap_api.client.GitRepository;

import java.util.List;

final public class GitRepositoriesEntity implements GitRepositories {

    private List<GitRepositoryEntity> repositories;

    public GitRepositoriesEntity(@JsonProperty("repositories") List<GitRepositoryEntity> repositories){
        this.repositories = repositories;
    }

    @Override
    public List<? extends GitRepository> getGitRepositories() {
        return this.repositories;
    }
}
