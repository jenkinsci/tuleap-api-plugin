package io.jenkins.plugins.tuleap_api.client;

import com.google.common.collect.ImmutableList;
import io.jenkins.plugins.tuleap_api.client.authentication.AccessToken;
import io.jenkins.plugins.tuleap_api.client.exceptions.ProjectNotFoundException;

public interface ProjectApi {

    String PROJECT_API = "/projects";
    String PROJECT_GROUPS = "/user_groups";

    Project getProjectByShortname(String shortname, AccessToken token) throws ProjectNotFoundException;
    ImmutableList<UserGroup> getProjectUserGroups(Integer projectId, AccessToken token);
}
