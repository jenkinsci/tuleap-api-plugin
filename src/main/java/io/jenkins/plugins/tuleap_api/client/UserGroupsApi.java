package io.jenkins.plugins.tuleap_api.client;

import io.jenkins.plugins.tuleap_api.client.authentication.AccessToken;

public interface UserGroupsApi {
    String USER_GROUPS_API = "/user_groups";

    UserGroup getUserGroup(String groupId, AccessToken accessToken);
}
