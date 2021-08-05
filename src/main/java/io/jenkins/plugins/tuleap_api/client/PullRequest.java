package io.jenkins.plugins.tuleap_api.client;

public interface PullRequest {
    String getId();

    GitRepositoryReference getSourceRepository();

    GitRepositoryReference getDestinationRepository();

    String getSourceBranch();

    String getDestinationBranch();

    String getSourceReference();

    String getDestinationReference();

    String getStatus();

    GitHead getHead();
}
