package io.jenkins.plugins.tuleap_api.client;

import com.google.common.collect.ImmutableList;
import hudson.util.Secret;
import io.jenkins.plugins.tuleap_api.client.authentication.AccessToken;

public interface UserApi {
    String USER_API = "/users";
    String USER_MEMBERSHIP = "/membership";
    String USER_SELF_ID = "/self";

    User getUserForAccessKey(Secret secret);
    @Deprecated ImmutableList<UserGroup> getUserMembershipName(AccessToken accessToken);
    ImmutableList<UserGroup> getUserMembership(AccessToken accessToken);
}
