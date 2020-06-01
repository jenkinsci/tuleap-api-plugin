package io.jenkins.plugins.tuleap_api.client.internals.entities.authentication.validators;

import io.jenkins.plugins.tuleap_api.client.authentication.AccessToken;
import io.jenkins.plugins.tuleap_api.client.internals.exceptions.InvalidHeaderException;
import io.jenkins.plugins.tuleap_api.client.internals.exceptions.InvalidIDTokenException;
import okhttp3.Response;

public interface AccessTokenValidator {
    void validateAccessTokenHeader(Response response) throws InvalidHeaderException;

    void validateAccessTokenBody(AccessToken accessToken) throws InvalidHeaderException;

    void validateIDToken(AccessToken accessToken) throws InvalidIDTokenException;
}
