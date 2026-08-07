package io.jenkins.plugins.tuleap_api.client.internals.entities.authentication.validators;

import io.jenkins.plugins.tuleap_api.client.authentication.UserInfo;
import io.jenkins.plugins.tuleap_api.client.internals.exceptions.InvalidHeaderException;
import okhttp3.Response;

import java.util.logging.Logger;
import hudson.Util;

public class UserInfoValidatorImpl implements UserInfoValidator {

    private static final Logger LOGGER = Logger.getLogger(UserInfoValidator.class.getName());

    private static final String CONTENT_TYPE_HEADER_VALUE = "application/json;charset=utf-8";

    @Override
    public void validateUserInfoResponseBody(UserInfo userInfo) throws InvalidHeaderException {
        if (Util.fixEmptyAndTrim(userInfo.getSubject()) == null) {
            LOGGER.warning("sub parameter is missing");
            throw new InvalidHeaderException("sub parameter is missing");
        }
    }

    @Override
    public void validateUserInfoHandshake(Response response) throws InvalidHeaderException {
        if (response.handshake() == null) {
            throw new InvalidHeaderException("TLS is not used");
        }
    }
}

