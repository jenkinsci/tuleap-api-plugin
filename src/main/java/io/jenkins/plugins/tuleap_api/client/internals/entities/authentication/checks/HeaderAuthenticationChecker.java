package io.jenkins.plugins.tuleap_api.client.internals.entities.authentication.checks;

import io.jenkins.plugins.tuleap_api.client.internals.exceptions.MalformedHeaderException;
import okhttp3.Response;

public interface HeaderAuthenticationChecker {
    void checkAccessTokenHeader(Response response) throws MalformedHeaderException;

    void checkUserInfoHandshake(Response response) throws MalformedHeaderException;

    void checkResponseHeader(Response response) throws MalformedHeaderException;
}
