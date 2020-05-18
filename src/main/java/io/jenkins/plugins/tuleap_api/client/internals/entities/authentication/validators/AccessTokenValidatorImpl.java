package io.jenkins.plugins.tuleap_api.client.internals.entities.authentication.validators;

import io.jenkins.plugins.tuleap_api.client.authentication.AccessToken;
import io.jenkins.plugins.tuleap_api.client.internals.exceptions.InvalidHeaderException;
import okhttp3.Response;
import org.apache.commons.lang.StringUtils;

import java.util.logging.Logger;

public class AccessTokenValidatorImpl implements AccessTokenValidator {

    private static final Logger LOGGER = Logger.getLogger(AccessTokenValidator.class.getName());

    private static final String PRAGMA_HEADER_VALUE = "no-cache";

    @Override
    public void validateAccessTokenHeader(Response response) throws InvalidHeaderException {
        if (!response.cacheControl().noStore()) {
            throw new InvalidHeaderException("Bad cache policy");
        }

        String pragma = response.header("Pragma");
        if (StringUtils.isBlank(pragma)) {
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

        if (StringUtils.isBlank(accessToken.getAccessToken())) {
            throw new InvalidHeaderException("Access token missing");
        }

        if (StringUtils.isBlank(accessToken.getTokenType())) {
            throw new InvalidHeaderException("Token type missing");
        }

        if (StringUtils.isBlank(accessToken.getExpiresIn())) {
            throw new InvalidHeaderException("No expiration date returned");
        }

        if (StringUtils.isBlank(accessToken.getIdToken())) {
            throw new InvalidHeaderException("No id token returned");
        }
    }
}
