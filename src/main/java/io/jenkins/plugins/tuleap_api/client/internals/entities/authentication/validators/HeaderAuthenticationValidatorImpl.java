package io.jenkins.plugins.tuleap_api.client.internals.entities.authentication.validators;

import io.jenkins.plugins.tuleap_api.client.internals.exceptions.InvalidHeaderException;
import okhttp3.Response;
import org.apache.commons.lang.StringUtils;

import java.util.logging.Logger;

public class HeaderAuthenticationValidatorImpl implements HeaderAuthenticationValidator {

    private static final Logger LOGGER = Logger.getLogger(HeaderAuthenticationValidator.class.getName());

    private static final String CONTENT_TYPE_HEADER_VALUE = "application/json;charset=utf-8";

    @Override
    public void validateHeader(Response response) throws InvalidHeaderException {
        String contentType = response.header("Content-type");
        if (StringUtils.isBlank(contentType)) {
            throw new InvalidHeaderException("There is no content type");
        }

        if (!contentType.toLowerCase().equals(CONTENT_TYPE_HEADER_VALUE.toLowerCase())) {
            throw new InvalidHeaderException("Bad content type value");
        }
    }
}
