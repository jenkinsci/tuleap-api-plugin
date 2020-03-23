package io.jenkins.plugins.tuleap_api.deprecated_client.impl;

import okhttp3.*;
import io.jenkins.plugins.tuleap_credentials.TuleapAccessToken;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

class AccessKeyInterceptor implements Interceptor {

    private final TuleapAccessToken token;

    AccessKeyInterceptor(final TuleapAccessToken token) {
        this.token = token;
    }

    @NotNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request req = chain.request();
        Request authenticatedReq = req.newBuilder().cacheControl(CacheControl.FORCE_NETWORK)
            .addHeader("Cache-Control", "no-cache").header("X-Auth-AccessKey", token.getToken().getPlainText()).build();
        return chain.proceed(authenticatedReq);
    }
}
