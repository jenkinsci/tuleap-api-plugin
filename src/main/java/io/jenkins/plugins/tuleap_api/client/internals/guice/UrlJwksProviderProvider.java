package io.jenkins.plugins.tuleap_api.client.internals.guice;

import com.auth0.jwk.UrlJwkProvider;
import com.google.inject.Inject;
import com.google.inject.Provider;
import io.jenkins.plugins.tuleap_server_configuration.TuleapConfiguration;

import java.net.MalformedURLException;
import java.net.URL;


public class UrlJwksProviderProvider implements Provider<UrlJwkProvider> {

    private TuleapConfiguration tuleapConfiguration;

    @Inject
    public UrlJwksProviderProvider(TuleapConfiguration tuleapConfiguration){
        this.tuleapConfiguration = tuleapConfiguration;
    }

    @Override
    public UrlJwkProvider get() {
        try {
            return new UrlJwkProvider(new URL(this.tuleapConfiguration.getDomainUrl() + "/oauth2/jwks"));
        } catch (MalformedURLException e) {
         throw  new RuntimeException(e.getMessage());
        }
    }
}
