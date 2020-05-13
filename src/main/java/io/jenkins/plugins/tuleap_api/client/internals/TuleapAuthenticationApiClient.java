package io.jenkins.plugins.tuleap_api.client.internals;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.SigningKeyNotFoundException;
import com.auth0.jwk.UrlJwkProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.util.Secret;
import io.jenkins.plugins.tuleap_api.client.authentication.*;
import io.jenkins.plugins.tuleap_api.client.internals.entities.authentication.AccessTokenEntity;
import io.jenkins.plugins.tuleap_api.client.internals.entities.authentication.OpenIdDiscoveryEntity;
import io.jenkins.plugins.tuleap_api.client.internals.entities.authentication.UserInfoEntity;
import io.jenkins.plugins.tuleap_api.client.internals.entities.authentication.checks.HeaderAuthenticationChecker;
import io.jenkins.plugins.tuleap_api.client.internals.exceptions.InvalidTuleapResponseException;
import io.jenkins.plugins.tuleap_api.client.internals.exceptions.MalformedHeaderException;
import io.jenkins.plugins.tuleap_api.client.internals.helper.PluginHelper;
import io.jenkins.plugins.tuleap_server_configuration.TuleapConfiguration;
import okhttp3.*;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class TuleapAuthenticationApiClient implements AccessTokenApi, UserInfoApi, OpenIDClientApi {

    public static final Logger LOGGER = Logger.getLogger(TuleapAuthenticationApiClient.class.getName());

    private final PluginHelper pluginHelper;
    private final OkHttpClient client;
    private final ObjectMapper objectMapper;
    private final UrlJwkProvider jwkProvider;
    private final TuleapConfiguration tuleapConfiguration;
    private final HeaderAuthenticationChecker headerAuthenticationChecker;

    @Inject
    public TuleapAuthenticationApiClient(
        PluginHelper pluginHelper,
        OkHttpClient client,
        ObjectMapper objectMapper,
        UrlJwkProvider jwkProvider,
        TuleapConfiguration tuleapConfiguration,
        HeaderAuthenticationChecker headerAuthenticationChecker
    ) {
        this.pluginHelper = pluginHelper;
        this.client = client;
        this.objectMapper = objectMapper;
        this.jwkProvider = jwkProvider;
        this.tuleapConfiguration = tuleapConfiguration;
        this.headerAuthenticationChecker = headerAuthenticationChecker;
    }

    @Override
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE") // see https://github.com/spotbugs/spotbugs/issues/651
    public AccessToken getAccessToken(
        String codeVerifier,
        String authorizationCode,
        String clientId,
        Secret clientSecret
    ) {
        Request accessTokenRequest = this.buildAccessTokenRequestBody(authorizationCode, codeVerifier, clientId, clientSecret);

        try (Response response = this.client.newCall(accessTokenRequest).execute()) {
            if (!response.isSuccessful()) {
                throw new InvalidTuleapResponseException(response);
            }
            this.headerAuthenticationChecker.checkAccessTokenHeader(response);
            return this.objectMapper.readValue(Objects.requireNonNull(response.body()).string(), AccessTokenEntity.class);
        } catch (IOException | InvalidTuleapResponseException | MalformedHeaderException exception) {
            LOGGER.severe(exception.getMessage());
            throw new RuntimeException("Error while contacting Tuleap server", exception);
        }
    }


    private Request buildAccessTokenRequestBody(
        String authorizationCode,
        String codeVerifier,
        String clientId,
        Secret clientSecret
    ) {
        RequestBody requestBody = new FormBody.Builder()
            .add("grant_type", "authorization_code")
            .add("code", authorizationCode)
            .add("code_verifier", codeVerifier)
            .addEncoded("redirect_uri", this.pluginHelper.getJenkinsInstance().getRootUrl() + AccessTokenApi.REDIRECT_URI)
            .build();

        return new Request.Builder()
            .url(this.tuleapConfiguration.getDomainUrl() + ACCESS_TOKEN_API)
            .addHeader("Authorization", Credentials.basic(clientId, clientSecret.getPlainText()))
            .addHeader("Content-Type", ACCESS_TOKEN_CONTENT_TYPE)
            .post(requestBody)
            .build();
    }

    @Override
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE") // see https://github.com/spotbugs/spotbugs/issues/651
    public List<Jwk> getSigningKeys() {
        try {
            return ImmutableList.copyOf(this.jwkProvider.getAll());
        } catch (SigningKeyNotFoundException e) {
            LOGGER.warning("No signing key found");
            return ImmutableList.of();
        }
    }

    @Override
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE") // see https://github.com/spotbugs/spotbugs/issues/651
    public UserInfo getUserInfo(AccessToken accessToken) {
        Request req = new Request.Builder()
            .url(this.tuleapConfiguration.getDomainUrl() + UserInfoApi.USER_INFO_ENDPOINT)
            .addHeader("Authorization", "Bearer " + accessToken.getAccessToken())
            .get()
            .build();

        try (Response response = this.client.newCall(req).execute()) {
            if (!response.isSuccessful()) {
                throw new InvalidTuleapResponseException(response);
            }
            this.headerAuthenticationChecker.checkUserInfoHandshake(response);
            this.headerAuthenticationChecker.checkResponseHeader(response);
            return this.objectMapper.readValue(Objects.requireNonNull(response.body()).string(), UserInfoEntity.class);
        } catch (IOException | InvalidTuleapResponseException | MalformedHeaderException e) {
            LOGGER.severe(e.getMessage());
            throw new RuntimeException("Error while contacting Tuleap server", e);
        }
    }


    @Override
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE") // see https://github.com/spotbugs/spotbugs/issues/651
    public String getProviderIssuer() {
        Request req = new Request.Builder()
            .url(this.tuleapConfiguration.getDomainUrl() + DISCOVERY_API)
            .get()
            .build();
        try (Response response = this.client.newCall(req).execute()) {
            if (!response.isSuccessful()) {
                throw new InvalidTuleapResponseException(response);
            }
            OpenIdDiscovery configuration = this.objectMapper.readValue(
                Objects.requireNonNull(response.body()).string(),
                OpenIdDiscoveryEntity.class
            );
            return configuration.getIssuer();
        } catch (IOException | InvalidTuleapResponseException e) {
            LOGGER.severe(e.getMessage());
            throw new RuntimeException("Error while contacting Tuleap server", e);
        }
    }
}
