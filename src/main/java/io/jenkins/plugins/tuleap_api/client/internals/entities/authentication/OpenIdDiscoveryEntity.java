package io.jenkins.plugins.tuleap_api.client.internals.entities.authentication;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.jenkins.plugins.tuleap_api.client.authentication.OpenIdDiscovery;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenIdDiscoveryEntity implements OpenIdDiscovery {

    private String issuer;

    public OpenIdDiscoveryEntity(@JsonProperty("issuer") String issuer){
        this.issuer = issuer;
    }

    @Override
    public String getIssuer() {
        return this.issuer;
    }
}
