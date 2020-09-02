package io.jenkins.plugins.tuleap_api.client;

import hudson.util.Secret;
import io.jenkins.plugins.tuleap_api.client.authentication.AccessToken;

import java.util.List;

public interface UserApi {
    String USER_API = "/users";
    String USER_MEMBERSHIP = "/membership";
    String USER_SELF_ID = "/self";

    User getUserForAccessKey(Secret secret);
    @Deprecated
    List<UserGroup> getUserMembershipName(AccessToken accessToken);
    List<UserGroup> getUserMembership(AccessToken accessToken);
}
