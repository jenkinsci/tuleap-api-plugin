package io.jenkins.plugins.tuleap_api.client;

import io.jenkins.plugins.tuleap_credentials.TuleapAccessToken;

public interface PullRequestApi {
    String PULL_REQUEST_API = "/pull_requests";

    PullRequest getPullRequest(String pullRequestId, TuleapAccessToken accessToken);
}
