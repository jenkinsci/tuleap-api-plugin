package io.jenkins.plugins.tuleap_api.client;

import hudson.util.Secret;
import java.util.List;

public interface AccessKeyApi {
    String ACCESS_KEY_API = "/access_keys";
    String ACCESS_KEY_SELF_ID = "/self";

    Boolean checkAccessKeyIsValid(Secret secret);

    List<AccessKeyScope> getAccessKeyScopes(Secret secret);
}
