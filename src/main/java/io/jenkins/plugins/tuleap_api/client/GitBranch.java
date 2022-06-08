package io.jenkins.plugins.tuleap_api.client;

public interface GitBranch {
    String getName();

    GitCommit getCommit();
}
