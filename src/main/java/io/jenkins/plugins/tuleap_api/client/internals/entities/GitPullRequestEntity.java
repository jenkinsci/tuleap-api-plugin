package io.jenkins.plugins.tuleap_api.client.internals.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.jenkins.plugins.tuleap_api.client.GitPullRequest;
import io.jenkins.plugins.tuleap_api.client.GitRepositoryReference;

public class GitPullRequestEntity implements GitPullRequest {

    private final String id;
    private final GitRepositoryReference sourceRepository;
    private final GitRepositoryReference destinationRepository;
    private final String sourceBranch;
    private final String destinationBranch;

    public GitPullRequestEntity(
        @JsonProperty("id") String id,
        @JsonProperty("repository") GitRepositoryReferenceEntity sourceRepository,
        @JsonProperty("repository_dest") GitRepositoryReferenceEntity destinationRepository,
        @JsonProperty("branch_src") String sourceBranch,
        @JsonProperty("branch_dest") String destinationBranch) {
        this.id = id;
        this.sourceRepository = sourceRepository;
        this.destinationRepository = destinationRepository;
        this.sourceBranch = sourceBranch;
        this.destinationBranch = destinationBranch;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public GitRepositoryReference getSourceRepository() {
        return this.sourceRepository;
    }

    @Override
    public GitRepositoryReference getDestinationRepository() {
        return this.destinationRepository;
    }

    @Override
    public String getSourceBranch() {
        return this.sourceBranch;
    }

    @Override
    public String getDestinationBranch() {
        return this.destinationBranch;
    }
}
