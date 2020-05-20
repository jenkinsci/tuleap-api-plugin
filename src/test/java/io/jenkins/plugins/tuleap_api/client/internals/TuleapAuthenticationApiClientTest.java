package io.jenkins.plugins.tuleap_api.client.internals;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.SigningKeyNotFoundException;
import com.auth0.jwk.UrlJwkProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import hudson.util.Secret;
import io.jenkins.plugins.tuleap_api.client.authentication.AccessToken;
import io.jenkins.plugins.tuleap_api.client.internals.entities.authentication.AccessTokenEntity;
import io.jenkins.plugins.tuleap_api.client.internals.entities.authentication.OpenIdDiscoveryEntity;
import io.jenkins.plugins.tuleap_api.client.internals.entities.authentication.UserInfoEntity;
import io.jenkins.plugins.tuleap_api.client.internals.entities.authentication.validators.AccessTokenValidator;
import io.jenkins.plugins.tuleap_api.client.internals.entities.authentication.validators.HeaderAuthenticationValidator;
import io.jenkins.plugins.tuleap_api.client.internals.entities.authentication.validators.UserInfoValidator;
import io.jenkins.plugins.tuleap_api.client.internals.helper.PluginHelper;
import io.jenkins.plugins.tuleap_server_configuration.TuleapConfiguration;
import jenkins.model.Jenkins;
import okhttp3.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TuleapAuthenticationApiClientTest {

    private PluginHelper pluginHelper;
    private OkHttpClient client;
    private ObjectMapper objectMapper;
    private UrlJwkProvider jwkProvider;
    private TuleapConfiguration tuleapConfiguration;
    private HeaderAuthenticationValidator headerAuthenticationChecker;
    private AccessTokenValidator accessTokenValidator;
    private UserInfoValidator userInfoValidator;

    @Before
    public void setUp() {
        this.pluginHelper = mock(PluginHelper.class);
        this.client = mock(OkHttpClient.class);
        this.objectMapper = mock(ObjectMapper.class);
        this.jwkProvider = mock(UrlJwkProvider.class);
        this.tuleapConfiguration = mock(TuleapConfiguration.class);
        this.headerAuthenticationChecker = mock(HeaderAuthenticationValidator.class);
        this.accessTokenValidator = mock(AccessTokenValidator.class);
        this.userInfoValidator = mock(UserInfoValidator.class);
    }

    @Test(expected = RuntimeException.class)
    public void testItThrowsExceptionsIfTheResponseFailed() throws IOException {
        Response response = mock(Response.class);
        when(response.isSuccessful()).thenReturn(false);

        Call call = mock(Call.class);
        when(this.client.newCall(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);

        TuleapAuthenticationApiAuthenticationClient authenticationApiClient = new TuleapAuthenticationApiAuthenticationClient(
            this.pluginHelper,
            this.client,
            this.objectMapper,
            this.jwkProvider,
            this.tuleapConfiguration,
            this.headerAuthenticationChecker,
            this.accessTokenValidator,
            this.userInfoValidator
        );

        Secret secret = Secret.fromString("1234");
        authenticationApiClient.getAccessToken("1234", "auth", "12374", secret);
    }

    @Test
    public void testItReturnsTheAccessToken() throws IOException {
        Jenkins jenkins = mock(Jenkins.class);
        when(this.pluginHelper.getJenkinsInstance()).thenReturn(jenkins);
        when(jenkins.getRootUrl()).thenReturn("https://jenkins.example.com");
        when(this.tuleapConfiguration.getDomainUrl()).thenReturn("https://tuleap.example.com");

        Response response = mock(Response.class);
        when(response.isSuccessful()).thenReturn(true);

        Call call = mock(Call.class);
        when(this.client.newCall(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);

        ResponseBody responseBody = mock(ResponseBody.class);
        when(response.body()).thenReturn(responseBody);
        when(responseBody.string()).thenReturn("{access_token:access, expires_in:3600, id_token:id_token, token_type: bearer}");

        AccessTokenEntity expectedAccessToken = new AccessTokenEntity("access","3600","id_token","bearer");
        when(this.objectMapper.readValue(Objects.requireNonNull(response.body()).string(), AccessTokenEntity.class)).thenReturn(expectedAccessToken);

        TuleapAuthenticationApiAuthenticationClient authenticationApiClient = new TuleapAuthenticationApiAuthenticationClient(
            this.pluginHelper,
            this.client,
            this.objectMapper,
            this.jwkProvider,
            this.tuleapConfiguration,
            this.headerAuthenticationChecker,
            this.accessTokenValidator,
            this.userInfoValidator
        );

        Secret secret = mock(Secret.class);
        when(secret.getPlainText()).thenReturn("12434");

        AccessToken accessToken = authenticationApiClient.getAccessToken("1234", "auth", "12374", secret);
        assertEquals(expectedAccessToken, accessToken);
    }

    @Test
    public void testItShouldReturnsNoSigningKey() throws SigningKeyNotFoundException {
        when(this.jwkProvider.getAll()).thenThrow(SigningKeyNotFoundException.class);

        TuleapAuthenticationApiAuthenticationClient authenticationApiClient = new TuleapAuthenticationApiAuthenticationClient(
            this.pluginHelper,
            this.client,
            this.objectMapper,
            this.jwkProvider,
            this.tuleapConfiguration,
            this.headerAuthenticationChecker,
            this.accessTokenValidator,
            this.userInfoValidator
        );

        assertEquals(ImmutableList.of(), authenticationApiClient.getSigningKeys());
    }

    @Test
    public void testItShouldReturnsSigningKeys() throws SigningKeyNotFoundException {
        Jwk key1 = mock(Jwk.class);
        Jwk key2 = mock(Jwk.class);

        when(this.jwkProvider.getAll()).thenReturn(Arrays.asList(key1, key2));

        TuleapAuthenticationApiAuthenticationClient authenticationApiClient = new TuleapAuthenticationApiAuthenticationClient(
            this.pluginHelper,
            this.client,
            this.objectMapper,
            this.jwkProvider,
            this.tuleapConfiguration,
            this.headerAuthenticationChecker,
            this.accessTokenValidator,
            this.userInfoValidator
        );

        assertEquals(ImmutableList.copyOf(Arrays.asList(key1, key2)), authenticationApiClient.getSigningKeys());
    }

    @Test(expected = RuntimeException.class)
    public void testItThrowsExceptionWhenTheUserInfoCannotBeRetrieved() throws IOException {
        when(this.tuleapConfiguration.getDomainUrl()).thenReturn("https://tuleap.example.com");

        AccessToken accessToken = mock(AccessToken.class);
        when(accessToken.getAccessToken()).thenReturn("1234");

        Response response = mock(Response.class);
        when(response.isSuccessful()).thenReturn(false);

        Call call = mock(Call.class);
        when(this.client.newCall(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);

        TuleapAuthenticationApiAuthenticationClient authenticationApiClient = new TuleapAuthenticationApiAuthenticationClient(
            this.pluginHelper,
            this.client,
            this.objectMapper,
            this.jwkProvider,
            this.tuleapConfiguration,
            this.headerAuthenticationChecker,
            this.accessTokenValidator,
            this.userInfoValidator
        );
        authenticationApiClient.getUserInfo(accessToken);
    }

    @Test
    public void testItReturnsTheUserInfo() throws IOException {
        when(this.tuleapConfiguration.getDomainUrl()).thenReturn("https://tuleap.example.com");

        AccessToken accessToken = mock(AccessToken.class);
        when(accessToken.getAccessToken()).thenReturn("1234");

        Response response = mock(Response.class);
        when(response.isSuccessful()).thenReturn(true);

        Call call = mock(Call.class);
        when(this.client.newCall(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);

        ResponseBody responseBody = mock(ResponseBody.class);
        when(response.body()).thenReturn(responseBody);
        when(responseBody.string()).thenReturn("{sub: 102, issuer:https://example.com}");

        UserInfoEntity expectedUserInfo = new UserInfoEntity("102", "https://example.com");
        when(this.objectMapper.readValue(Objects.requireNonNull(response.body()).string(), UserInfoEntity.class)).thenReturn(expectedUserInfo);

        TuleapAuthenticationApiAuthenticationClient authenticationApiClient = new TuleapAuthenticationApiAuthenticationClient(
            this.pluginHelper,
            this.client,
            this.objectMapper,
            this.jwkProvider,
            this.tuleapConfiguration,
            this.headerAuthenticationChecker,
            this.accessTokenValidator,
            this.userInfoValidator
        );

        assertEquals(expectedUserInfo, authenticationApiClient.getUserInfo(accessToken));
    }

    @Test(expected = RuntimeException.class)
    public void testItThrowsExceptionWhenTheIssuerCannotBeRetrieved() throws IOException {
        when(this.tuleapConfiguration.getDomainUrl()).thenReturn("https://tuleap.example.com");

        Response response = mock(Response.class);
        when(response.isSuccessful()).thenReturn(false);

        Call call = mock(Call.class);
        when(this.client.newCall(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);

        TuleapAuthenticationApiAuthenticationClient authenticationApiClient = new TuleapAuthenticationApiAuthenticationClient(
            this.pluginHelper,
            this.client,
            this.objectMapper,
            this.jwkProvider,
            this.tuleapConfiguration,
            this.headerAuthenticationChecker,
            this.accessTokenValidator,
            this.userInfoValidator
        );

        authenticationApiClient.getProviderIssuer();
    }

    @Test
    public void testItReturnsTheIssuer() throws IOException {
        when(this.tuleapConfiguration.getDomainUrl()).thenReturn("https://tuleap.example.com");

        AccessToken accessToken = mock(AccessToken.class);
        when(accessToken.getAccessToken()).thenReturn("1234");

        Response response = mock(Response.class);
        when(response.isSuccessful()).thenReturn(true);

        Call call = mock(Call.class);
        when(this.client.newCall(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);

        ResponseBody responseBody = mock(ResponseBody.class);
        when(response.body()).thenReturn(responseBody);
        when(responseBody.string()).thenReturn("{issuer:https://example.com}");

        OpenIdDiscoveryEntity configuration = new OpenIdDiscoveryEntity("https://example.com");
        when(this.objectMapper.readValue(Objects.requireNonNull(response.body()).string(),OpenIdDiscoveryEntity.class)).thenReturn(configuration);

        TuleapAuthenticationApiAuthenticationClient authenticationApiClient = new TuleapAuthenticationApiAuthenticationClient(
            this.pluginHelper,
            this.client,
            this.objectMapper,
            this.jwkProvider,
            this.tuleapConfiguration,
            this.headerAuthenticationChecker,
            this.accessTokenValidator,
            this.userInfoValidator
        );

        assertEquals("https://example.com", authenticationApiClient.getProviderIssuer());
    }

}
