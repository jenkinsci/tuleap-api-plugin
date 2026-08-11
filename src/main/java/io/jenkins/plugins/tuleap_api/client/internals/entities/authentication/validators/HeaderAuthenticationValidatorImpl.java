package io.jenkins.plugins.tuleap_api.client.internals.entities.authentication.validators;

import io.jenkins.plugins.tuleap_api.client.internals.exceptions.InvalidHeaderException;
import okhttp3.Response;

import java.util.logging.Logger;
import hudson.Util;

public class HeaderAuthenticationValidatorImpl implements HeaderAuthenticationValidator {

    private static final Logger LOGGER = Logger.getLogger(HeaderAuthenticationValidator.class.getName());

    private static final String CONTENT_TYPE_HEADER_VALUE = "application/json;charset=utf-8";

    @Override
    public void validateHeader(Response response) throws InvalidHeaderException {
        String contentType = response.header("Content-type");
        if (Util.fixEmptyAndTrim(contentType) == null) {
            throw new InvalidHeaderException("There is no content type");
        }

        if (!contentType.equalsIgnoreCase(CONTENT_TYPE_HEADER_VALUE)) {
            throw new InvalidHeaderException("Bad content type value");
        }
    }
}
