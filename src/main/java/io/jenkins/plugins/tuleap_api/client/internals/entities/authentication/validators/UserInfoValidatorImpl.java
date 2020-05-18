package io.jenkins.plugins.tuleap_api.client.internals.entities.authentication.validators;

import io.jenkins.plugins.tuleap_api.client.authentication.UserInfo;
import io.jenkins.plugins.tuleap_api.client.internals.exceptions.InvalidHeaderException;
import okhttp3.Response;
import org.apache.commons.lang.StringUtils;

import java.util.logging.Logger;

public class UserInfoValidatorImpl implements UserInfoValidator {

    private static final Logger LOGGER = Logger.getLogger(UserInfoValidator.class.getName());

    private static final String CONTENT_TYPE_HEADER_VALUE = "application/json;charset=utf-8";

    @Override
    public void validateUserInfoResponseBody(UserInfo userInfo) throws InvalidHeaderException {
        if (StringUtils.isBlank(userInfo.getSubject())) {
            LOGGER.warning("sub parameter is missing");
            throw new InvalidHeaderException("sub parameter is missing");
        }
    }

    @Override
    public void validateUserInfoHandshake(Response response) throws InvalidHeaderException {
        if (response.handshake() == null) {
            LOGGER.severe("TLS is not used");
            throw new InvalidHeaderException("TLS is not used");
        }
    }
}

