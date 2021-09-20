package io.jenkins.plugins.tuleap_api.client.internals.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.jenkins.plugins.tuleap_api.client.GitHead;
import io.jenkins.plugins.tuleap_api.client.GitPullRequest;
import io.jenkins.plugins.tuleap_api.client.GitRepositoryReference;
import io.jenkins.plugins.tuleap_api.client.PullRequest;

public class PullRequestEntity implements PullRequest {

    private final String id;
    private final GitRepositoryReference sourceRepository;
    private final GitRepositoryReference destinationRepository;
    private final String sourceBranch;
    private final String destinationBranch;
    private final String sourceReference;
    private final String destinationReference;
    private final String status;
    private final String headReference;
    private final GitHeadEntity head;

    public PullRequestEntity(
        @JsonProperty("id") String id,
        @JsonProperty("repository") GitRepositoryReferenceEntity sourceRepository,
        @JsonProperty("repository_dest") GitRepositoryReferenceEntity destinationRepository,
        @JsonProperty("branch_src") String sourceBranch,
        @JsonProperty("branch_dest") String destinationBranch,
        @JsonProperty("reference_src") String sourceReference,
        @JsonProperty("reference_dest") String destinationReference,
        @JsonProperty("status") String status,
        @JsonProperty("head_reference") String headReference,
        @JsonProperty("head") GitHeadEntity head
    ) {
        this.id = id;
        this.sourceRepository = sourceRepository;
        this.destinationRepository = destinationRepository;
        this.sourceBranch = sourceBranch;
        this.destinationBranch = destinationBranch;
        this.sourceReference = sourceReference;
        this.destinationReference = destinationReference;
        this.status = status;
        this.headReference = headReference;
        this.head = head;
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

    @Override
    public String getSourceReference() {
        return this.sourceReference;
    }

    @Override
    public String getDestinationReference() {
        return this.destinationReference;
    }

    @Override
    public String getStatus() {
        return this.status;
    }

    @Override
    public String getHeadReference() {
        return this.headReference;
    }

    @Override
    public GitHead getHead() {
        return this.head;
    }
}
