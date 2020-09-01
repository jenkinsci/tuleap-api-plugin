package io.jenkins.plugins.tuleap_credentials;

import com.google.inject.Inject;
import hudson.util.Secret;
import io.jenkins.plugins.tuleap_api.client.AccessKeyApi;
import io.jenkins.plugins.tuleap_api.client.AccessKeyScope;
import io.jenkins.plugins.tuleap_credentials.exceptions.InvalidAccessKeyException;
import io.jenkins.plugins.tuleap_credentials.exceptions.InvalidScopesForAccessKeyException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AccessKeyChecker {
    private static List<String> MANDATORY_SCOPES = Collections.unmodifiableList(Arrays.asList("write:rest", "write:git_repository"));

    private AccessKeyApi client;

    @Inject
    public AccessKeyChecker(AccessKeyApi client) {
        this.client = client;
    }

    public void verifyAccessKey(Secret secret) throws InvalidAccessKeyException, InvalidScopesForAccessKeyException {
        if (! accessKeyIsValid(secret)) {
            throw new InvalidAccessKeyException();
        }

        if (! scopesAreValid(secret)) {
            throw new InvalidScopesForAccessKeyException();
        }
    }

    private Boolean accessKeyIsValid(Secret secret) {
        return client.checkAccessKeyIsValid(secret);
    }

    private Boolean scopesAreValid(Secret secret) {
        return client
        .getAccessKeyScopes(secret)
        .stream()
        .map(AccessKeyScope::getIdentifier)
        .collect(Collectors.toList())
        .containsAll(MANDATORY_SCOPES);
    }
}
