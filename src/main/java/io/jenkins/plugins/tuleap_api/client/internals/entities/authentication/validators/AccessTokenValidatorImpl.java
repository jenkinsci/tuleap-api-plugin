package io.jenkins.plugins.tuleap_api.client.internals.entities.authentication.validators;

import io.jenkins.plugins.tuleap_api.client.authentication.AccessToken;
import io.jenkins.plugins.tuleap_api.client.internals.exceptions.InvalidHeaderException;
import io.jenkins.plugins.tuleap_api.client.internals.exceptions.InvalidIDTokenException;
import okhttp3.Response;

import java.util.logging.Logger;
import hudson.Util;

public class AccessTokenValidatorImpl implements AccessTokenValidator {

    private static final Logger LOGGER = Logger.getLogger(AccessTokenValidator.class.getName());

    private static final String PRAGMA_HEADER_VALUE = "no-cache";

    @Override
    public void validateAccessTokenHeader(Response response) throws InvalidHeaderException {
        if (!response.cacheControl().noStore()) {
            throw new InvalidHeaderException("Bad cache policy");
        }

        String pragma = response.header("Pragma");
        if (Util.fixEmptyAndTrim(pragma) == null) {
            throw new InvalidHeaderException("Pragma header missing");
        }

        if (!pragma.equals(PRAGMA_HEADER_VALUE)) {
            throw new InvalidHeaderException("Bad pragma value");
        }
    }

    @Override
    public void validateAccessTokenBody(AccessToken accessToken) throws InvalidHeaderException {
        if (accessToken == null) {
            throw new InvalidHeaderException("There is no body");
        }

        if (Util.fixEmptyAndTrim(accessToken.getAccessToken()) == null) {
            throw new InvalidHeaderException("Access token missing");
        }

        if (Util.fixEmptyAndTrim(accessToken.getTokenType()) == null) {
            throw new InvalidHeaderException("Token type missing");
        }

        if (Util.fixEmptyAndTrim(accessToken.getExpiresIn()) == null) {
            throw new InvalidHeaderException("No expiration date returned");
        }
    }

    @Override
    public void validateIDToken(AccessToken accessToken) throws InvalidIDTokenException {
        if (Util.fixEmptyAndTrim(accessToken.getIdToken()) == null) {
            throw new InvalidIDTokenException("No id token returned");
        }
    }
}
