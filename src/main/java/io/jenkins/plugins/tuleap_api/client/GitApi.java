package io.jenkins.plugins.tuleap_api.client;

import io.jenkins.plugins.tuleap_api.client.internals.entities.TuleapBuildStatus;
import io.jenkins.plugins.tuleap_credentials.TuleapAccessToken;
import org.jenkinsci.plugins.plaincredentials.StringCredentials;

public interface GitApi {
    String GIT_API = "/git";
    String STATUSES = "/statuses";

    void sendBuildStatus(String repositoryId, String commitReference, TuleapBuildStatus status, StringCredentials credentials);

    void sendBuildStatus(String repositoryId, String commitReference, TuleapBuildStatus status, TuleapAccessToken token);
}
