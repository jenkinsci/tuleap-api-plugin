package io.jenkins.plugins.tuleap_api.client.internals.entities.authentication.checks;

import io.jenkins.plugins.tuleap_api.client.internals.exceptions.MalformedHeaderException;
import okhttp3.Response;
import org.apache.commons.lang.StringUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

public class HeaderAuthenticationCheckerImpl implements HeaderAuthenticationChecker {

    private static final Logger LOGGER = Logger.getLogger(HeaderAuthenticationChecker.class.getName());

    private static final String CONTENT_TYPE_HEADER_VALUE = "application/json;charset=utf-8";
    private static final String PRAGMA_HEADER_VALUE = "no-cache";

    @Override
    public void checkAccessTokenHeader(Response response) throws MalformedHeaderException {
        this.checkResponseHeader(response);
        if (!response.cacheControl().noStore()) {
            LOGGER.log(Level.WARNING, "Bad cache policy");
            throw new MalformedHeaderException("Bad cache policy");
        }

        String pragma = response.header("Pragma");
        if (StringUtils.isBlank(pragma)) {
            LOGGER.log(Level.WARNING, "Pragma header missing");
            throw new MalformedHeaderException("Pragma header missing");
        }

        if (!pragma.equals(PRAGMA_HEADER_VALUE)) {
            LOGGER.log(Level.WARNING, "Bad pragma value");
            throw new MalformedHeaderException("Bad pragma value");
        }
    }

    @Override
    public void checkUserInfoHandshake(Response response) throws MalformedHeaderException {
        if (response.handshake() == null) {
            LOGGER.severe("TLS is not used");
            throw new MalformedHeaderException("TLS is not used");
        }
    }

    @Override
    public void checkResponseHeader(Response response) throws MalformedHeaderException {
        String contentType = response.header("Content-type");
        if (StringUtils.isBlank(contentType)) {
            LOGGER.severe("There is no content type");
            throw new MalformedHeaderException("There is no content type");
        }

        if (!contentType.toLowerCase().equals(CONTENT_TYPE_HEADER_VALUE.toLowerCase())) {
            LOGGER.severe("Bad content type value" + contentType);
            throw new MalformedHeaderException("Bad content type value");
        }
    }
}
