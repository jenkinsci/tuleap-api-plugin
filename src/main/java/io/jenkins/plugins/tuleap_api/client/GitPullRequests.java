package io.jenkins.plugins.tuleap_api.client;

import java.util.List;

public interface GitPullRequests {
    List<? extends GitPullRequest> getPullRequests();
}
