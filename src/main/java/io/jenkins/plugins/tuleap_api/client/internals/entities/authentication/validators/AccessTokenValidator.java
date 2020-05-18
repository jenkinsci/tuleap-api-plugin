package io.jenkins.plugins.tuleap_api.client.internals.entities.authentication.validators;

import io.jenkins.plugins.tuleap_api.client.authentication.AccessToken;
import io.jenkins.plugins.tuleap_api.client.internals.exceptions.InvalidHeaderException;
import okhttp3.Response;

public interface AccessTokenValidator {
    void validateAccessTokenHeader(Response response) throws InvalidHeaderException;

    void validateAccessTokenBody(AccessToken accessToken) throws InvalidHeaderException;
}
