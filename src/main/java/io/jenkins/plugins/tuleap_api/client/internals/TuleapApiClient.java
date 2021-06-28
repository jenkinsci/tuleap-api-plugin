package io.jenkins.plugins.tuleap_api.client.internals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.inject.Inject;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.util.Secret;
import io.jenkins.plugins.tuleap_api.client.*;
import io.jenkins.plugins.tuleap_api.client.authentication.AccessToken;
import io.jenkins.plugins.tuleap_api.client.exceptions.ProjectNotFoundException;
import io.jenkins.plugins.tuleap_api.client.internals.entities.*;
import io.jenkins.plugins.tuleap_api.client.internals.exceptions.InvalidTuleapResponseException;
import io.jenkins.plugins.tuleap_credentials.TuleapAccessToken;
import io.jenkins.plugins.tuleap_server_configuration.TuleapConfiguration;
import okhttp3.*;
import org.jenkinsci.plugins.plaincredentials.StringCredentials;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class TuleapApiClient implements TuleapAuthorization, AccessKeyApi, UserApi, UserGroupsApi, ProjectApi , TestCampaignApi, GitApi {
    private static final Logger LOGGER = Logger.getLogger(TuleapApiClient.class.getName());
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

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
    public List<AccessKeyScope> getAccessKeyScopes(Secret secret) {
        Request request = new Request.Builder()
            .url(tuleapConfiguration.getApiBaseUrl() + this.ACCESS_KEY_API + this.ACCESS_KEY_SELF_ID)
            .header(this.AUTHORIZATION_HEADER, secret.getPlainText())
            .get()
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (! response.isSuccessful()) {
                throw new InvalidTuleapResponseException(response);
            }

            return new ArrayList<>(objectMapper
                .readValue(Objects.requireNonNull(response.body()).string(), AccessKeyEntity.class)
                .getScopes()
            );
        } catch (IOException | InvalidTuleapResponseException exception) {
            LOGGER.severe(exception.getMessage());
            return Collections.emptyList();
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

    /**
     * @deprecated Use getUserMembership() instead. If you still use this method you should update your Tuleap and use the getUserMembership() which is more efficent.
     */
    @Deprecated
    @Override
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE") // see https://github.com/spotbugs/spotbugs/issues/651
    public List<UserGroup> getUserMembershipName(AccessToken accessToken) {
        LOGGER.info("You are using a deprecated method. Please upgrade your Tuleap and use getUserMembership() instead. ");

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
        List<String> userMembershipIds = Collections.emptyList();
        try (Response response = this.client.newCall(req).execute()) {
            if (!response.isSuccessful()) {
                throw new InvalidTuleapResponseException(response);
            }
            userMembershipIds =
                new ArrayList<>(
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

        return memberships;
    }

    @Override
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE") // see https://github.com/spotbugs/spotbugs/issues/651
    public List<UserGroup> getUserMembership(AccessToken accessToken) {
        HttpUrl urlUserMembership = Objects.requireNonNull(HttpUrl.parse(this.tuleapConfiguration.getApiBaseUrl() + this.USER_API + this.USER_SELF_ID + this.USER_MEMBERSHIP))
            .newBuilder()
            .addEncodedQueryParameter("scope", "project")
            .addEncodedQueryParameter("format", "full")
            .build();

        Request req = new Request.Builder()
            .url(urlUserMembership)
            .addHeader("Authorization", "Bearer " + accessToken.getAccessToken())
            .get()
            .build();

        try (Response response = this.client.newCall(req).execute()) {

            if (response.code() == 400) {
                return this.getUserMembershipName(accessToken);
            }

            if (!response.isSuccessful()) {
                throw new InvalidTuleapResponseException(response);
            }

            return new ArrayList<>(
                this.objectMapper.readValue(
                    Objects.requireNonNull(response.body()).string(),
                    new TypeReference<List<UserGroupEntity>>() {
                    }
                ));
        } catch (IOException | InvalidTuleapResponseException e) {
            throw new RuntimeException("Error while contacting Tuleap server", e);
        }
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

    @Override
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE") // see https://github.com/spotbugs/spotbugs/issues/651
    public Project getProjectByShortname(String shortname, AccessToken token) throws ProjectNotFoundException {
        final HttpUrl url = Objects.requireNonNull(HttpUrl.parse(this.tuleapConfiguration.getApiBaseUrl() + this.PROJECT_API))
            .newBuilder()
            .addEncodedQueryParameter("limit", "1")
            .addEncodedQueryParameter("query", String.format("{\"shortname\":\"%s\"}", shortname))
            .build();

        final Request request = new Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer " + token.getAccessToken())
            .get()
            .build();

        try (final Response response = this.client.newCall(request).execute()) {
            if (! response.isSuccessful()) {
                throw new InvalidTuleapResponseException(response);
            }

            final List<ProjectEntity> projects = this.objectMapper.readValue(
                Objects.requireNonNull(response.body()).string(),
                new TypeReference<List<ProjectEntity>>() {}
            );

            return projects
                .stream()
                .findFirst()
                .orElseThrow(() -> new ProjectNotFoundException(shortname));
        } catch (IOException | InvalidTuleapResponseException e) {
            throw new RuntimeException("Error while contacting Tuleap server", e);
        }
    }

    @Override
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE") // see https://github.com/spotbugs/spotbugs/issues/651
    public List<UserGroup> getProjectUserGroups(Integer projectId, AccessToken token) {
        final Request request = new Request.Builder()
            .url(this.tuleapConfiguration.getApiBaseUrl() + this.PROJECT_API + "/" + projectId + this.PROJECT_GROUPS)
            .addHeader("Authorization", "Bearer " + token.getAccessToken())
            .get()
            .build();

        try (final Response response = this.client.newCall(request).execute()) {
            if (! response.isSuccessful()) {
                throw new InvalidTuleapResponseException(response);
            }

            return new ArrayList<>(this.objectMapper.readValue(
                Objects.requireNonNull(response.body()).string(),
                new TypeReference<List<MinimalUserGroupEntity>>() {}
            ));
        } catch (IOException | InvalidTuleapResponseException e) {
            throw new RuntimeException("Error while contacting Tuleap server", e);
        }
    }

    @Override
    public void sendTTMResults(String campaignId, String buildUrl, List<String> results, Secret secret) {
        Request request;

        try {
            request = new Request.Builder()
                .url(this.tuleapConfiguration.getApiBaseUrl() + this.TEST_CAMPAIGN_API + "/" + campaignId)
                .addHeader(this.AUTHORIZATION_HEADER, secret.getPlainText())
                .patch(RequestBody.create(this.objectMapper.writer(SerializationFeature.WRAP_ROOT_VALUE).writeValueAsString(new SendTTMResultsEntity(buildUrl,results)), JSON))
                .build();
        } catch (JsonProcessingException exception) {
            throw new RuntimeException("Error while trying to create request for TTM results", exception);
        }

        try (Response response = this.client.newCall(request).execute()) {
            if (! response.isSuccessful()) {
                throw new InvalidTuleapResponseException(response);
            }
        } catch (IOException | InvalidTuleapResponseException exception) {
            throw new RuntimeException("Error while contacting Tuleap server", exception);
        }
    }

    @Override
    public void sendBuildStatus(String repositoryId, String commitReference, TuleapBuildStatus status, StringCredentials credentials) {
        Request request;

        try {
            request = new Request.Builder()
                .url(this.tuleapConfiguration.getApiBaseUrl() + this.GIT_API + "/" + repositoryId + this.STATUSES + "/" + commitReference )
                .post(RequestBody.create(this.objectMapper.writeValueAsString(new SendBuildStatusAndCITokenEntity(status.name(), credentials.getSecret().getPlainText())), JSON))
                .build();
        } catch (JsonProcessingException exception) {
            throw new RuntimeException("Error while trying to create request for build status", exception);
        }

        try (Response response = this.client.newCall(request).execute()) {
            if (! response.isSuccessful()) {
                throw new InvalidTuleapResponseException(response);
            }
        } catch (IOException | InvalidTuleapResponseException exception) {
            throw new RuntimeException("Error while contacting Tuleap server", exception);
        }
    }

    @Override
    public void sendBuildStatus(String repositoryId, String commitReference, TuleapBuildStatus status, TuleapAccessToken token) {
        Request request;

        try {
            request = new Request.Builder()
                .url(this.tuleapConfiguration.getApiBaseUrl() + this.GIT_API + "/" + repositoryId + this.STATUSES + "/" + commitReference)
                .addHeader(this.AUTHORIZATION_HEADER, token.getToken().getPlainText())
                .post(RequestBody.create(this.objectMapper.writeValueAsString(new SendBuildStatusEntity(status.name())), JSON))
                .build();
        } catch (JsonProcessingException exception) {
            throw new RuntimeException("Error while trying to create request for build status", exception);
        }

        try (Response response = this.client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new InvalidTuleapResponseException(response);
            }
        } catch (IOException | InvalidTuleapResponseException exception) {
            throw new RuntimeException("Error while contacting Tuleap server", exception);
        }
    }

    @Override
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE") // see https://github.com/spotbugs/spotbugs/issues/651
    public GitCommit getCommit(String repositoryId, String commitReference, TuleapAccessToken token) {
        Request request = new Request.Builder()
            .url(this.tuleapConfiguration.getApiBaseUrl() + this.GIT_API + "/" + repositoryId + this.COMMITS + "/" + commitReference)
            .addHeader(this.AUTHORIZATION_HEADER, token.getToken().getPlainText())
            .get()
            .build();

        try (Response response = this.client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new InvalidTuleapResponseException(response);
            }
            return this.objectMapper.readValue(
                Objects.requireNonNull(response.body()).string(),
                GitCommitEntity.class
            );
        } catch (IOException | InvalidTuleapResponseException e) {
            throw new RuntimeException("Error while contacting Tuleap server", e);
        }
    }

    @Override
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE") // see https://github.com/spotbugs/spotbugs/issues/651
    public List<GitTreeContent> getTree(String repositoryId, String commitReference, String path, TuleapAccessToken token) {
        HttpUrl urlGetTree = Objects.requireNonNull(HttpUrl.parse(this.tuleapConfiguration.getApiBaseUrl() + this.GIT_API + repositoryId + this.TREE))
            .newBuilder()
            .addEncodedQueryParameter("path", path)
            .addEncodedQueryParameter("ref", commitReference)
            .build();

        Request request = new Request.Builder()
            .url(urlGetTree)
            .addHeader(this.AUTHORIZATION_HEADER, token.getToken().getPlainText())
            .get()
            .build();

        try (final Response response = this.client.newCall(request).execute()) {
            if (! response.isSuccessful()) {
                throw new InvalidTuleapResponseException(response);
            }

            return new ArrayList<>(this.objectMapper.readValue(
                Objects.requireNonNull(response.body()).string(),
                new TypeReference<List<GitTreeContentEntity>>() {}
            ));
        } catch (IOException | InvalidTuleapResponseException e) {
            throw new RuntimeException("Error while contacting Tuleap server", e);
        }
    }

    @Override
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE") // see https://github.com/spotbugs/spotbugs/issues/651
    public List<GitTreeContent> getTree(String repositoryId, String commitReference, String path, TuleapAccessToken token) {
        HttpUrl urlGetTree = Objects.requireNonNull(HttpUrl.parse(this.tuleapConfiguration.getApiBaseUrl() + this.GIT_API + repositoryId + this.TREE))
            .newBuilder()
            .addEncodedQueryParameter("path", path)
            .addEncodedQueryParameter("ref", commitReference)
            .build();

        Request request = new Request.Builder()
            .url(urlGetTree)
            .addHeader(this.AUTHORIZATION_HEADER, token.getToken().getPlainText())
            .get()
            .build();

        try (final Response response = this.client.newCall(request).execute()) {
            if (! response.isSuccessful()) {
                throw new InvalidTuleapResponseException(response);
            }

            return new ArrayList<>(this.objectMapper.readValue(
                Objects.requireNonNull(response.body()).string(),
                new TypeReference<List<GitTreeContentEntity>>() {}
            ));
        } catch (IOException | InvalidTuleapResponseException e) {
            throw new RuntimeException("Error while contacting Tuleap server", e);
        }
    }
}
