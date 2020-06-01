package io.jenkins.plugins.tuleap_api.client.internals.entities.authentication;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.jenkins.plugins.tuleap_api.client.authentication.AccessToken;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessTokenEntity implements AccessToken {

    private String accessToken;
    private String tokenType;
    private String expiresIn;
    private String refreshToken;
    private String idToken;

    public AccessTokenEntity(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("expires_in") String expiresIn,
        @JsonProperty("refresh_token") String refreshToken,
        @JsonProperty("id_token") String idToken
    ) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.refreshToken = refreshToken;
        this.idToken = idToken;
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
    public String getRefreshToken() {
        return this.refreshToken;
    }

    @Override
    public String getIdToken() {
        return this.idToken;
    }
}
