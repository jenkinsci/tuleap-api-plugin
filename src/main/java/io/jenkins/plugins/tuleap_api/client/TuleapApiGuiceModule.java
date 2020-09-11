package io.jenkins.plugins.tuleap_api.client;

import io.jenkins.plugins.tuleap_api.client.internals.TuleapApiClient;
import io.jenkins.plugins.tuleap_api.client.internals.guice.OkHttpClientProvider;
import okhttp3.OkHttpClient;

public class TuleapApiGuiceModule extends com.google.inject.AbstractModule {
    @Override
    protected void configure() {
        bind(OkHttpClient.class).toProvider(OkHttpClientProvider.class).asEagerSingleton();
        bind(AccessKeyApi.class).to(TuleapApiClient.class);
        bind(UserApi.class).to(TuleapApiClient.class);
        bind(UserGroupsApi.class).to(TuleapApiClient.class);
        bind(ProjectApi.class).to(TuleapApiClient.class);
        bind(TestCampaignApi.class).to(TuleapApiClient.class);
        bind(GitApi.class).to(TuleapApiClient.class);
    }
}
