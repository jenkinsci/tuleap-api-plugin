package io.jenkins.plugins.tuleap_api.client.internals;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.util.Secret;
import io.jenkins.plugins.tuleap_api.client.*;
import io.jenkins.plugins.tuleap_api.client.authentication.AccessToken;
import io.jenkins.plugins.tuleap_api.client.internals.entities.AccessKeyEntity;
import io.jenkins.plugins.tuleap_api.client.internals.entities.UserEntity;
import io.jenkins.plugins.tuleap_api.client.internals.entities.UserGroupEntity;
import io.jenkins.plugins.tuleap_api.client.internals.exceptions.InvalidTuleapResponseException;
import io.jenkins.plugins.tuleap_server_configuration.TuleapConfiguration;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class TuleapApiClient implements TuleapAuthorization, AccessKeyApi, UserApi, UserGroupsApi {
    private static final Logger LOGGER = Logger.getLogger(TuleapApiClient.class.getName());

    private OkHttpClient client;

    private TuleapConfiguration tuleapConfiguration;

    private ObjectMapper objectMapper;

    @Inject
    public TuleapApiClient(
        TuleapConfiguration tuleapConfiguration,
        OkHttpClient client,
        ObjectMapper objectMapper
    ) {
        this.tuleapConfiguration = tuleapConfiguration;
        this.client = client;
        this.objectMapper = objectMapper;
    }

    @Override
    public Boolean checkAccessKeyIsValid(Secret secret) {
        Request request = new Request.Builder()
            .url(tuleapConfiguration.getApiBaseUrl() + this.ACCESS_KEY_API + this.ACCESS_KEY_SELF_ID)
            .header(this.AUTHORIZATION_HEADER, secret.getPlainText())
            .get()
            .build();

        try (Response response = client.newCall(request).execute()) {
            return response.code() == 200;
        } catch (IOException exception) {
            return false;
        }
    }

    @Override
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE") // see https://github.com/spotbugs/spotbugs/issues/651
    public ImmutableList<AccessKeyScope> getAccessKeyScopes(Secret secret) {
        Request request = new Request.Builder()
            .url(tuleapConfiguration.getApiBaseUrl() + this.ACCESS_KEY_API + this.ACCESS_KEY_SELF_ID)
            .header(this.AUTHORIZATION_HEADER, secret.getPlainText())
            .get()
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (! response.isSuccessful()) {
                throw new InvalidTuleapResponseException(response);
            }

            return ImmutableList.copyOf(
                objectMapper
                .readValue(Objects.requireNonNull(response.body()).string(), AccessKeyEntity.class)
                .getScopes()
            );
        } catch (IOException | InvalidTuleapResponseException exception) {
            LOGGER.severe(exception.getMessage());
            return ImmutableList.of();
        }
    }

    @Override
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE") // see https://github.com/spotbugs/spotbugs/issues/651
    public User getUserForAccessKey(Secret secret) {
        Request request = new Request.Builder()
            .url(tuleapConfiguration.getApiBaseUrl() + this.USER_API + this.USER_SELF_ID)
            .header(this.AUTHORIZATION_HEADER, secret.getPlainText())
            .get()
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (! response.isSuccessful()) {
                throw new InvalidTuleapResponseException(response);
            }

            return objectMapper
                .readValue(Objects.requireNonNull(response.body()).string(), UserEntity.class);

        } catch (IOException | InvalidTuleapResponseException exception) {
            throw new RuntimeException("Error while contacting Tuleap server", exception);
        }
    }

    @Override
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE") // see https://github.com/spotbugs/spotbugs/issues/651
    public ImmutableList<UserGroup> getUserMembershipName(AccessToken accessToken) {
        HttpUrl urlUserMembership = Objects.requireNonNull(HttpUrl.parse(this.tuleapConfiguration.getApiBaseUrl() + this.USER_API + this.USER_SELF_ID + this.USER_MEMBERSHIP))
            .newBuilder()
            .addEncodedQueryParameter("scope", "project")
            .addEncodedQueryParameter("format", "id")
            .build();

        Request req = new Request.Builder()
            .url(urlUserMembership)
            .addHeader("Authorization", "Bearer " + accessToken.getAccessToken())
            .get()
            .build();
        List<String> userMembershipIds = ImmutableList.of();
        try (Response response = this.client.newCall(req).execute()) {
            if (!response.isSuccessful()) {
                throw new InvalidTuleapResponseException(response);
            }
            userMembershipIds =
                ImmutableList.copyOf(
                    this.objectMapper.readValue(
                        Objects.requireNonNull(response.body()).string(),
                        new TypeReference<List<String>>() {
                        }
                    ));
        } catch (IOException | InvalidTuleapResponseException e) {
            throw new RuntimeException("Error while contacting Tuleap server", e);
        }

        List<UserGroup> memberships = new ArrayList<>();
        userMembershipIds.forEach(groupId -> {
            memberships.add(this.getUserGroup(groupId, accessToken));
        });

        return ImmutableList.copyOf(memberships);
    }

    @Override
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE") // see https://github.com/spotbugs/spotbugs/issues/651
    public UserGroup getUserGroup(String groupId, AccessToken accessToken) {
        Request request = new Request.Builder()
            .url(this.tuleapConfiguration.getApiBaseUrl() + this.USER_GROUPS_API + "/" + groupId)
            .addHeader("Authorization", "Bearer " + accessToken.getAccessToken())
            .get()
            .build();

        try (Response response = this.client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new InvalidTuleapResponseException(response);
            }
            return this.objectMapper.readValue(
                Objects.requireNonNull(response.body()).string(),
                UserGroupEntity.class
            );
        } catch (IOException | InvalidTuleapResponseException e) {
            throw new RuntimeException("Error while contacting Tuleap server", e);
        }
    }
}
