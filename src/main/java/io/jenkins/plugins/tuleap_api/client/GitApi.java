package io.jenkins.plugins.tuleap_api.client;

import hudson.util.Secret;
import io.jenkins.plugins.tuleap_api.client.internals.entities.TuleapBuildStatus;

public interface GitApi {
    String GIT_API = "/git";
    String STATUSES = "/statuses";

    void sendBuildStatus(String repositoryId, String commitReference, TuleapBuildStatus status, Secret token);
}
