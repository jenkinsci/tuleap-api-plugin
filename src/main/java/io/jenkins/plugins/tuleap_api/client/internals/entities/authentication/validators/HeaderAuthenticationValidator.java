package io.jenkins.plugins.tuleap_api.client.internals.entities.authentication.validators;

import io.jenkins.plugins.tuleap_api.client.internals.exceptions.InvalidHeaderException;
import okhttp3.Response;

public interface HeaderAuthenticationValidator {
    void validateHeader(Response response) throws InvalidHeaderException;
}
