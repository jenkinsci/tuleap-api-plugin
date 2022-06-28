package io.jenkins.plugins.tuleap_api.client;

import io.jenkins.plugins.tuleap_api.client.authentication.AccessToken;
import io.jenkins.plugins.tuleap_api.client.exceptions.ProjectNotFoundException;
import io.jenkins.plugins.tuleap_credentials.TuleapAccessToken;

import java.util.List;

public interface ProjectApi {

    String PROJECT_API = "/projects";
    String PROJECT_GROUPS = "/user_groups";
    String PROJECT_GIT = "/git";

    String PROJECT_MEMBER_OF_QUERY = "{\"is_member_of\":true}";

    Project getProjectByShortname(String shortname, AccessToken token) throws ProjectNotFoundException;
    Project getProjectById(String projectId, TuleapAccessToken token) ;
    List<UserGroup> getProjectUserGroups(Integer projectId, AccessToken token);
    List<GitRepository> getGitRepositories(Integer projectId, TuleapAccessToken token);
    List<Project> getUserProjects(TuleapAccessToken token);
}
