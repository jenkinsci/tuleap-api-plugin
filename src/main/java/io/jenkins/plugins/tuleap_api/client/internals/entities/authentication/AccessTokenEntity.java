package io.jenkins.plugins.tuleap_api.client.internals.entities.authentication;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.jenkins.plugins.tuleap_api.client.authentication.AccessToken;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessTokenEntity implements AccessToken {

    private String accessToken;
    private String tokenType;
    private String expiresIn;
    private String idToken;

    public AccessTokenEntity(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("expires_in") String expiresIn,
        @JsonProperty("id_token") String idToken
    ) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.idToken = idToken;
    }

    @Override
    public String getAccessToken() {
        return accessToken;
    }

    @Override
    public String getTokenType() {
        return tokenType;
    }

    @Override
    public String getExpiresIn() {
        return expiresIn;
    }

    @Override
    public String getIdToken() {
        return idToken;
    }
}
