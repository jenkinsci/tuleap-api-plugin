package io.jenkins.plugins.tuleap_credentials;

import com.google.inject.Inject;
import hudson.util.Secret;
import io.jenkins.plugins.tuleap_api.client.UserApi;

public class UsernameRetriever {
    private final UserApi client;

    @Inject
    public UsernameRetriever(final UserApi client) {
        this.client = client;
    }

    public String getUsernameForToken(final Secret token) {
        return client.getUserForAccessKey(token).getUsername();
    }
}
