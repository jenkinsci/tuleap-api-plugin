package io.jenkins.plugins.tuleap_api.client.internals;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.SigningKeyNotFoundException;
import com.auth0.jwk.UrlJwkProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.util.Secret;
import io.jenkins.plugins.tuleap_api.client.authentication.*;
import io.jenkins.plugins.tuleap_api.client.internals.entities.authentication.AccessTokenEntity;
import io.jenkins.plugins.tuleap_api.client.internals.entities.authentication.OpenIdDiscoveryEntity;
import io.jenkins.plugins.tuleap_api.client.internals.entities.authentication.UserInfoEntity;
import io.jenkins.plugins.tuleap_api.client.internals.entities.authentication.validators.AccessTokenValidator;
import io.jenkins.plugins.tuleap_api.client.internals.entities.authentication.validators.HeaderAuthenticationValidator;
import io.jenkins.plugins.tuleap_api.client.internals.entities.authentication.validators.UserInfoValidator;
import io.jenkins.plugins.tuleap_api.client.internals.exceptions.InvalidIDTokenException;
import io.jenkins.plugins.tuleap_api.client.internals.exceptions.InvalidTuleapResponseException;
import io.jenkins.plugins.tuleap_api.client.internals.exceptions.InvalidHeaderException;
import io.jenkins.plugins.tuleap_api.client.internals.helper.PluginHelper;
import io.jenkins.plugins.tuleap_server_configuration.TuleapConfiguration;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class TuleapAuthenticationApiAuthenticationClient implements AccessTokenApi, OpenIDClientApi {

    public static final Logger LOGGER = Logger.getLogger(TuleapAuthenticationApiAuthenticationClient.class.getName());

    private final PluginHelper pluginHelper;
    private final OkHttpClient client;
    private final ObjectMapper objectMapper;
    private final UrlJwkProvider jwkProvider;
    private final TuleapConfiguration tuleapConfiguration;
    private final HeaderAuthenticationValidator headerAuthenticationValidator;
    private final AccessTokenValidator accessTokenValidator;
    private final UserInfoValidator userInfoValidator;

    @Inject
    public TuleapAuthenticationApiAuthenticationClient(
        PluginHelper pluginHelper,
        OkHttpClient client,
        ObjectMapper objectMapper,
        UrlJwkProvider jwkProvider,
        TuleapConfiguration tuleapConfiguration,
        HeaderAuthenticationValidator headerAuthenticationValidator,
        AccessTokenValidator accessTokenValidator,
        UserInfoValidator userInfoValidator
    ) {
        this.pluginHelper = pluginHelper;
        this.client = client;
        this.objectMapper = objectMapper;
        this.jwkProvider = jwkProvider;
        this.tuleapConfiguration = tuleapConfiguration;
        this.headerAuthenticationValidator = headerAuthenticationValidator;
        this.accessTokenValidator = accessTokenValidator;
        this.userInfoValidator = userInfoValidator;
    }

    @Override
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE") // see https://github.com/spotbugs/spotbugs/issues/651
    public AccessToken getAccessToken(
        String codeVerifier,
        String authorizationCode,
        String clientId,
        Secret clientSecret
    ) {
        RequestBody requestBody = new FormBody.Builder()
            .add("grant_type", "authorization_code")
            .add("code", authorizationCode)
            .add("code_verifier", codeVerifier)
            .addEncoded("redirect_uri", this.pluginHelper.getJenkinsInstance().getRootUrl() + AccessTokenApi.REDIRECT_URI)
            .build();

        Request accessTokenRequest = this.buildAccessTokenRequestBody(requestBody, clientId, clientSecret);

        try (Response response = this.client.newCall(accessTokenRequest).execute()) {
            if (!response.isSuccessful()) {
                throw new InvalidTuleapResponseException(response);
            }
            this.headerAuthenticationValidator.validateHeader(response);
            this.accessTokenValidator.validateAccessTokenHeader(response);
            AccessToken accessToken = this.objectMapper.readValue(Objects.requireNonNull(response.body()).string(), AccessTokenEntity.class);
            this.accessTokenValidator.validateAccessTokenBody(accessToken);
            this.accessTokenValidator.validateIDToken(accessToken);
            return accessToken;
        } catch (IOException | InvalidTuleapResponseException | InvalidHeaderException | InvalidIDTokenException exception) {
            throw new RuntimeException("Error while contacting Tuleap server", exception);
        }
    }

    @Override
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE") // see https://github.com/spotbugs/spotbugs/issues/651
    public AccessToken refreshToken(AccessToken accessToken, String clientId, Secret clientSecret) {
        RequestBody requestBody = new FormBody.Builder()
            .add("grant_type", "refresh_token")
            .add("refresh_token", accessToken.getRefreshToken())
            .addEncoded("scope", AccessTokenApi.REFRESH_TOKEN_SCOPES)
            .build();

        Request refreshTokenRequest = this.buildAccessTokenRequestBody(requestBody, clientId, clientSecret);

        try (Response response = this.client.newCall(refreshTokenRequest).execute()) {
            if (!response.isSuccessful()) {
                throw new InvalidTuleapResponseException(response);
            }
            this.headerAuthenticationValidator.validateHeader(response);
            this.accessTokenValidator.validateAccessTokenHeader(response);
            AccessToken refreshToken = this.objectMapper.readValue(Objects.requireNonNull(response.body()).string(), AccessTokenEntity.class);
            this.accessTokenValidator.validateAccessTokenBody(refreshToken);
            return refreshToken;
        } catch (IOException | InvalidTuleapResponseException | InvalidHeaderException exception) {
            throw new RuntimeException("Error while contacting Tuleap server", exception);
        }
    }


    private Request buildAccessTokenRequestBody(
        RequestBody requestBody,
        String clientId,
        Secret clientSecret
    ) {

        return new Request.Builder()
            .url(this.tuleapConfiguration.getDomainUrl() + ACCESS_TOKEN_API)
            .addHeader("Authorization", Credentials.basic(clientId, clientSecret.getPlainText()))
            .addHeader("Content-Type", ACCESS_TOKEN_CONTENT_TYPE)
            .post(requestBody)
            .build();
    }

    @Override
    public List<Jwk> getSigningKeys() {
        try {
            return this.jwkProvider.getAll();
        } catch (SigningKeyNotFoundException e) {
            LOGGER.warning("No signing key found");
            return Collections.emptyList();
        }
    }

    @Override
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE") // see https://github.com/spotbugs/spotbugs/issues/651
    public UserInfo getUserInfo(AccessToken accessToken) {
        Request req = new Request.Builder()
            .url(this.tuleapConfiguration.getDomainUrl() + OpenIDClientApi.USER_INFO_API)
            .addHeader("Authorization", "Bearer " + accessToken.getAccessToken())
            .get()
            .build();

        try (Response response = this.client.newCall(req).execute()) {
            if (!response.isSuccessful()) {
                throw new InvalidTuleapResponseException(response);
            }
            this.headerAuthenticationValidator.validateHeader(response);
            this.userInfoValidator.validateUserInfoHandshake(response);
            UserInfo userInfo = this.objectMapper.readValue(Objects.requireNonNull(response.body()).string(), UserInfoEntity.class);
            this.userInfoValidator.validateUserInfoResponseBody(userInfo);
            return userInfo;
        } catch (IOException | InvalidTuleapResponseException | InvalidHeaderException e) {
            throw new RuntimeException("Error while contacting Tuleap server", e);
        }
    }


    @Override
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE") // see https://github.com/spotbugs/spotbugs/issues/651
    public String getProviderIssuer() {
        Request req = new Request.Builder()
            .url(this.tuleapConfiguration.getDomainUrl() + OpenIDClientApi.DISCOVERY_API)
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
            throw new RuntimeException("Error while contacting Tuleap server", e);
        }
    }
}
