package io.jenkins.plugins.tuleap_api.client;

import java.util.List;

public interface GitRepositories {
    List<? extends GitRepository> getGitRepositories();
}
