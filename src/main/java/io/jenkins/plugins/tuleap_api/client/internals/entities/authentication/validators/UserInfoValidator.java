package io.jenkins.plugins.tuleap_api.client.internals.entities.authentication.validators;

import io.jenkins.plugins.tuleap_api.client.authentication.UserInfo;
import io.jenkins.plugins.tuleap_api.client.internals.exceptions.InvalidHeaderException;
import okhttp3.Response;

public interface UserInfoValidator {
    void validateUserInfoResponseBody(UserInfo userInfoRepresentation) throws InvalidHeaderException;

    void validateUserInfoHandshake(Response response) throws InvalidHeaderException;
}
