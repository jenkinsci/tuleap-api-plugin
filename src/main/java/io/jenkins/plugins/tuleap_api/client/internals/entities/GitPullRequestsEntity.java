package io.jenkins.plugins.tuleap_api.client.internals.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.jenkins.plugins.tuleap_api.client.GitPullRequest;
import io.jenkins.plugins.tuleap_api.client.GitPullRequests;

import java.util.List;

public class GitPullRequestsEntity implements GitPullRequests {
    private final List<GitPullRequestEntity> pullRequests;

    public GitPullRequestsEntity(@JsonProperty("collection") List<GitPullRequestEntity> pullRequests){
        this.pullRequests = pullRequests;
    }

    @Override
    public List<? extends GitPullRequest> getPullRequests() {
        return this.pullRequests;
    }
}
