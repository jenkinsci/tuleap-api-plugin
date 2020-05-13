package io.jenkins.plugins.tuleap_api.client.authentication;

import com.auth0.jwk.UrlJwkProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import io.jenkins.plugins.tuleap_api.client.internals.TuleapAuthenticationApiClient;
import io.jenkins.plugins.tuleap_api.client.internals.entities.authentication.checks.HeaderAuthenticationChecker;
import io.jenkins.plugins.tuleap_api.client.internals.entities.authentication.checks.HeaderAuthenticationCheckerImpl;
import io.jenkins.plugins.tuleap_api.client.internals.guice.JwksProvider;
import io.jenkins.plugins.tuleap_api.client.internals.guice.ObjectMapperProvider;
import io.jenkins.plugins.tuleap_api.client.internals.guice.OkHttpClientProvider;
import io.jenkins.plugins.tuleap_api.client.internals.helper.PluginHelper;
import io.jenkins.plugins.tuleap_api.client.internals.helper.PluginHelperimpl;
import okhttp3.OkHttpClient;

public class TuleapAuthenticationApiGuiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(OkHttpClient.class).toProvider(OkHttpClientProvider.class).asEagerSingleton();
        bind(ObjectMapper.class).toProvider(ObjectMapperProvider.class);
        bind(UrlJwkProvider.class).toProvider(JwksProvider.class);
        bind(PluginHelper.class).to(PluginHelperimpl.class);
        bind(AccessTokenApi.class).to(TuleapAuthenticationApiClient.class);
        bind(UserInfoApi.class).to(TuleapAuthenticationApiClient.class);
        bind(OpenIDClientApi.class).to(TuleapAuthenticationApiClient.class);
        bind(HeaderAuthenticationChecker.class).to(HeaderAuthenticationCheckerImpl.class);
    }
}
