package io.jenkins.plugins.tuleap_api.client;

import io.jenkins.plugins.tuleap_api.client.exceptions.git.FileContentNotFoundException;
import io.jenkins.plugins.tuleap_api.client.exceptions.git.TreeNotFoundException;
import io.jenkins.plugins.tuleap_api.client.internals.entities.TuleapBuildStatus;
import io.jenkins.plugins.tuleap_credentials.TuleapAccessToken;
import org.jenkinsci.plugins.plaincredentials.StringCredentials;

import java.util.List;

public interface GitApi {
    String GIT_API = "/git";

    String STATUSES = "/statuses";
    String COMMITS = "/commits";
    String TREE = "/tree";
    String FILES = "/files";
    String PULL_REQUEST = "/pull_requests";

    void sendBuildStatus(String repositoryId, String commitReference, TuleapBuildStatus status, StringCredentials credentials);

    void sendBuildStatus(String repositoryId, String commitReference, TuleapBuildStatus status, TuleapAccessToken token);

    GitCommit getCommit(String repositoryId, String commitReference, TuleapAccessToken token);

    List<GitTreeContent> getTree(String repositoryId, String commitReference, String path, TuleapAccessToken token) throws TreeNotFoundException;

    GitFileContent getFileContent(String repositoryId, String path, String commitReference, TuleapAccessToken token) throws FileContentNotFoundException;

    List<GitPullRequest> getPullRequests(String repositoryId, TuleapAccessToken token);
}
