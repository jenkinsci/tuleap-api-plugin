package io.jenkins.plugins.tuleap_api.client.internals.entities.authentication;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.jenkins.plugins.tuleap_api.client.authentication.TokenResponse;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenResponseEntity implements TokenResponse {
    private String accessToken;
    private String tokenType;
    private String expiresIn;
    private String idToken;
    private String refreshToken;

    public TokenResponseEntity(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("expires_in") String expiresIn,
        @JsonProperty("id_token") String idToken,
        @JsonProperty("refresh_token") String refreshToken
    ) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.idToken = idToken;
        this.refreshToken = refreshToken;
    }

    @Override
    public String getAccessToken() {
        return this.accessToken;
    }

    @Override
    public String getTokenType() {
        return this.tokenType;
    }

    @Override
    public String getExpiresIn() {
        return this.expiresIn;
    }

    @Override
    public String getIdToken() {
        return this.idToken;
    }

    @Override
    public String getRefreshToken() {
        return this.refreshToken;
    }
}
