package io.jenkins.plugins.tuleap_api.client;

import hudson.util.Secret;
import io.jenkins.plugins.tuleap_api.client.internals.entities.BuildStatus;

public interface GitApi {
    String GIT_API = "/git";
    String STATUSES = "/statuses";

    void sendBuildStatus(String repositoryId, String commitReference, BuildStatus status, Secret token);
}
