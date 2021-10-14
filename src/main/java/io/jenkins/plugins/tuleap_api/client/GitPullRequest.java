package io.jenkins.plugins.tuleap_api.client;

public interface GitPullRequest {
    String getId();

    String getTitle();

    GitRepositoryReference getSourceRepository();

    GitRepositoryReference getDestinationRepository();

    String getSourceBranch();

    String getDestinationBranch();

    GitHead getHead();
}
