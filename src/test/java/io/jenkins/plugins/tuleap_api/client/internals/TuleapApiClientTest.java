package io.jenkins.plugins.tuleap_api.client.internals;

import com.cloudbees.plugins.credentials.CredentialsDescriptor;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import hudson.util.Secret;
import io.jenkins.plugins.tuleap_api.client.*;
import io.jenkins.plugins.tuleap_api.client.authentication.AccessToken;
import io.jenkins.plugins.tuleap_api.client.exceptions.ProjectNotFoundException;
import io.jenkins.plugins.tuleap_api.client.exceptions.git.FileContentNotFoundException;
import io.jenkins.plugins.tuleap_api.client.exceptions.git.TreeNotFoundException;
import io.jenkins.plugins.tuleap_api.client.internals.entities.*;
import io.jenkins.plugins.tuleap_credentials.TuleapAccessToken;
import io.jenkins.plugins.tuleap_credentials.TuleapAccessTokenImpl;
import io.jenkins.plugins.tuleap_server_configuration.TuleapConfiguration;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TuleapApiClientTest {
    private TuleapConfiguration tuleapConfiguration;
    private OkHttpClient client;
    private ObjectMapper mapper;
    private TuleapApiClient tuleapApiClient;
    private Secret secret;

    private AccessToken accessToken;

    @Before
    public void setUp() {
        client = mock(OkHttpClient.class);
        tuleapConfiguration = mock(TuleapConfiguration.class);
        mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        tuleapApiClient = new TuleapApiClient(tuleapConfiguration, client, mapper);
        secret = mock(Secret.class);

        when(tuleapConfiguration.getApiBaseUrl()).thenReturn("https://example.tuleap.test");
        when(secret.getPlainText()).thenReturn("whatever");

        accessToken = mock(AccessToken.class);
        when(accessToken.getAccessToken()).thenReturn("access_to_tuleap_oauth2");
    }

    @Test
    public void itShouldReturnFalseIfTuleapServerDoesNotAnswer200() throws IOException {
        Call call = mock(Call.class);
        Response response = mock(Response.class);

        when(client.newCall(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.code()).thenReturn(400);

        assertFalse(tuleapApiClient.checkAccessKeyIsValid(secret));
    }

    @Test
    public void itShouldReturnTrueIfTuleapServerAnswers200() throws IOException {
        Call call = mock(Call.class);
        Response response = mock(Response.class);

        when(client.newCall(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.code()).thenReturn(200);

        assertTrue(tuleapApiClient.checkAccessKeyIsValid(secret));
    }

    @Test
    public void itShouldReturnAnEmptyScopesListIfCallIsNotSuccessfull() throws IOException {
        Call call = mock(Call.class);
        Response response = mock(Response.class);

        when(client.newCall(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.isSuccessful()).thenReturn(false);

        assertEquals(0, tuleapApiClient.getAccessKeyScopes(secret).size());
    }

    @Test
    public void itShouldReturnScopesFromTuleapServerResponse() throws IOException {
        Call call = mock(Call.class);
        Response response = mock(Response.class);
        ResponseBody responseBody = mock(ResponseBody.class);
        String json_payload = IOUtils.toString(TuleapApiClientTest.class.getResourceAsStream("access_key_payload.json"));

        when(client.newCall(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.isSuccessful()).thenReturn(true);
        when(response.body()).thenReturn(responseBody);
        when(responseBody.string()).thenReturn(json_payload);

        assertEquals(2, tuleapApiClient.getAccessKeyScopes(secret).size());
    }

    @Test(expected = RuntimeException.class)
    public void itShouldThrowAnExceptionWhenCallForUserIsNotSuccessfull() throws IOException {
        Call call = mock(Call.class);
        Response response = mock(Response.class);

        when(client.newCall(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.isSuccessful()).thenReturn(false);

        tuleapApiClient.getUserForAccessKey(secret);
    }

    @Test
    public void itShouldReturnAUserWhenCallForUserIsSuccessfull() throws IOException {
        Call call = mock(Call.class);
        Response response = mock(Response.class);
        ResponseBody responseBody = mock(ResponseBody.class);
        String json_payload = IOUtils.toString(TuleapApiClientTest.class.getResourceAsStream("user_payload.json"));

        when(client.newCall(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.isSuccessful()).thenReturn(true);
        when(response.body()).thenReturn(responseBody);
        when(responseBody.string()).thenReturn(json_payload);

        assertEquals("mjagger", tuleapApiClient.getUserForAccessKey(secret).getUsername());
    }

    @Test(expected = RuntimeException.class)
    public void itShouldThrowExceptionWhenTheResponseIsNotSuccessfulAtQueryingUserMembershipRoute() throws IOException {
        Call call = mock(Call.class);
        Response response = mock(Response.class);
        ResponseBody responseBody = mock(ResponseBody.class);

        when(client.newCall(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.isSuccessful()).thenReturn(false);

        tuleapApiClient.getUserMembershipName(accessToken);
    }

    @Test
    public void itShouldReturnTheUserMemberShipList() throws IOException {
        Call call = mock(Call.class);
        Response response = mock(Response.class);
        ResponseBody responseBody = mock(ResponseBody.class);
        String jsonUserMembershipPayload = IOUtils.toString(TuleapApiClientTest.class.getResourceAsStream("user_membership_payload.json"), UTF_8.name());
        String jsonUserGroupsPayload1 = IOUtils.toString(TuleapApiClientTest.class.getResourceAsStream("user_groups_payload1.json"), UTF_8.name());
        String jsonUserGroupsPayload2 = IOUtils.toString(TuleapApiClientTest.class.getResourceAsStream("user_groups_payload2.json"), UTF_8.name());
        String jsonUserGroupsPayload3 = IOUtils.toString(TuleapApiClientTest.class.getResourceAsStream("user_groups_payload3.json"), UTF_8.name());

        when(client.newCall(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.isSuccessful()).thenReturn(true);
        when(response.body()).thenReturn(responseBody);
        when(responseBody.string())
            .thenReturn(jsonUserMembershipPayload)
            .thenReturn(jsonUserGroupsPayload1)
            .thenReturn(jsonUserGroupsPayload2)
            .thenReturn(jsonUserGroupsPayload3);

        UserGroup userGroup1 = new UserGroupEntity("project_members", new ProjectEntity("coincoin", 22));
        UserGroup userGroup2 = new UserGroupEntity("project_admins", new ProjectEntity("coincoin", 22));
        UserGroup userGroup3 = new UserGroupEntity("project_members", new ProjectEntity("git-test", 33));

        List<UserGroup> expectedList = Arrays.asList(userGroup1, userGroup2, userGroup3);

        List<UserGroup> resultList = tuleapApiClient.getUserMembershipName(accessToken);
        assertEquals(resultList.get(0).getGroupName(), expectedList.get(0).getGroupName());
        assertEquals(resultList.get(0).getProjectName(), expectedList.get(0).getProjectName());

        assertEquals(resultList.get(1).getGroupName(), expectedList.get(1).getGroupName());
        assertEquals(resultList.get(1).getProjectName(), expectedList.get(1).getProjectName());

        assertEquals(resultList.get(2).getGroupName(), expectedList.get(2).getGroupName());
        assertEquals(resultList.get(2).getProjectName(), expectedList.get(2).getProjectName());
    }

    @Test
    public void itShouldCallGetUserMembershipNameIfTheServerReturns400() throws IOException {
        Call call = mock(Call.class);
        Response response = mock(Response.class);
        ResponseBody responseBody = mock(ResponseBody.class);

        when(client.newCall(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.code()).thenReturn(400);
        when(response.isSuccessful()).thenReturn(true);

        String jsonUserMembershipPayload = IOUtils.toString(TuleapApiClientTest.class.getResourceAsStream("user_membership_payload.json"), UTF_8.name());
        String jsonUserGroupsPayload1 = IOUtils.toString(TuleapApiClientTest.class.getResourceAsStream("user_groups_payload1.json"), UTF_8.name());

        when(client.newCall(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.isSuccessful()).thenReturn(true);
        when(response.body()).thenReturn(responseBody);
        when(responseBody.string())
            .thenReturn(jsonUserMembershipPayload)
            .thenReturn(jsonUserGroupsPayload1);

        UserGroup userGroup1 = new UserGroupEntity("project_members", new ProjectEntity("coincoin", 22));
        List<UserGroup> expectedList = Arrays.asList(userGroup1);

        List<UserGroup> resultList = this.tuleapApiClient.getUserMembership(this.accessToken);

        assertEquals(expectedList.get(0).getGroupName(), resultList.get(0).getGroupName());
        assertEquals(expectedList.get(0).getProjectName(), resultList.get(0).getProjectName());
    }

    @Test
    public void itShouldReturnUserMembership() throws IOException {
        Call call = mock(Call.class);
        Response response = mock(Response.class);
        ResponseBody responseBody = mock(ResponseBody.class);

        String projectMembershipPayload = IOUtils.toString(TuleapApiClientTest.class.getResourceAsStream("project_membership_payload.json"), UTF_8.name());

        when(client.newCall(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.code()).thenReturn(404);
        when(response.isSuccessful()).thenReturn(true);
        when(response.body()).thenReturn(responseBody);
        when(responseBody.string())
            .thenReturn(projectMembershipPayload);

        UserGroup userMembership1 = new UserGroupEntity("project_members", new ProjectEntity("coincoin", 106));
        UserGroup userMembership2 = new UserGroupEntity("atchoum", new ProjectEntity("git-test", 113));

        List<UserGroup> expectedList = Arrays.asList(userMembership1, userMembership2);

        List<UserGroup> resultList = tuleapApiClient.getUserMembership(this.accessToken);

        assertEquals(expectedList.get(0).getGroupName(), resultList.get(0).getGroupName());
        assertEquals(expectedList.get(0).getProjectName(), resultList.get(0).getProjectName());

        assertEquals(expectedList.get(1).getGroupName(), resultList.get(1).getGroupName());
        assertEquals(expectedList.get(1).getProjectName(), resultList.get(1).getProjectName());
    }

    @Test(expected = RuntimeException.class)
    public void itShouldThrowExceptionWhenTheResponseIsNotSuccessfulAtQueryingNewUserMembershipRoute() throws IOException {
        Call call = mock(Call.class);
        Response response = mock(Response.class);
        ResponseBody responseBody = mock(ResponseBody.class);

        when(client.newCall(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.code()).thenReturn(404);
        when(response.isSuccessful()).thenReturn(false);

        tuleapApiClient.getUserMembership(this.accessToken);
    }

    @Test(expected = RuntimeException.class)
    public void itThrowsExceptionWhenTheUserGroupCannotBeRetrieved() throws IOException {
        Call call = mock(Call.class);
        Response response = mock(Response.class);
        ResponseBody responseBody = mock(ResponseBody.class);

        when(client.newCall(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.isSuccessful()).thenReturn(false);

        tuleapApiClient.getUserGroup("1518", accessToken);
    }

    @Test
    public void itReturnsTheUserGroup() throws IOException {
        Call call = mock(Call.class);
        Response response = mock(Response.class);
        ResponseBody responseBody = mock(ResponseBody.class);
        String jsonUserGroupsPayload = IOUtils.toString(TuleapApiClientTest.class.getResourceAsStream("user_groups_payload1.json"), UTF_8.name());

        when(client.newCall(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.isSuccessful()).thenReturn(true);
        when(response.body()).thenReturn(responseBody);
        when(responseBody.string())
            .thenReturn(jsonUserGroupsPayload);

        UserGroup expectedUserGroup = new UserGroupEntity("project_members", new ProjectEntity("coincoin", 22));
        UserGroup resultUserGroup = tuleapApiClient.getUserGroup("106_3", accessToken);
        assertEquals(expectedUserGroup.getGroupName(), resultUserGroup.getGroupName());
        assertEquals(expectedUserGroup.getProjectName(), resultUserGroup.getProjectName());
    }

    @Test (expected = RuntimeException.class)
    public void itThrowsAnExceptionWhenProjectsCannotBeRetrieved() throws IOException, ProjectNotFoundException {
        Call call = mock(Call.class);
        Response response = mock(Response.class);
        ResponseBody responseBody = mock(ResponseBody.class);

        when(client.newCall(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.isSuccessful()).thenReturn(false);

        tuleapApiClient.getProjectByShortname("some-project-shortname", accessToken);
    }

    @Test (expected = ProjectNotFoundException.class)
    public void itThrowsAProjectNotFoundExceptionIfProjectCannotBeFound() throws IOException, ProjectNotFoundException {
        Call call = mock(Call.class);
        Response response = mock(Response.class);
        ResponseBody responseBody = mock(ResponseBody.class);
        String payload = IOUtils.toString(TuleapApiClientTest.class.getResourceAsStream("project_empty_payload.json"), UTF_8);

        when(client.newCall(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.isSuccessful()).thenReturn(true);
        when(response.body()).thenReturn(responseBody);
        when(responseBody.string()).thenReturn(payload);

        tuleapApiClient.getProjectByShortname("some-project-shortname", accessToken);
    }

    @Test
    public void itReturnsTheProjectCorrespondingToShortname() throws IOException, ProjectNotFoundException {
        Call call = mock(Call.class);
        Response response = mock(Response.class);
        ResponseBody responseBody = mock(ResponseBody.class);
        String payload = IOUtils.toString(TuleapApiClientTest.class.getResourceAsStream("project_payload.json"), UTF_8);

        when(client.newCall(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.isSuccessful()).thenReturn(true);
        when(response.body()).thenReturn(responseBody);
        when(responseBody.string()).thenReturn(payload);

        Project project = tuleapApiClient.getProjectByShortname("use-me", accessToken);

        assertEquals("use-me", project.getShortname());
    }

    @Test (expected = RuntimeException.class)
    public void itThrowsAnExceptionWhenProjectUserGroupsCannotBeRetrieved() throws IOException {
        Call call = mock(Call.class);
        Response response = mock(Response.class);
        ResponseBody responseBody = mock(ResponseBody.class);

        when(client.newCall(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.isSuccessful()).thenReturn(false);

        tuleapApiClient.getProjectUserGroups(20, accessToken);
    }

    @Test
    public void itShouldReturnProjectUserGroups() throws IOException {
        Call call = mock(Call.class);
        Response response = mock(Response.class);
        ResponseBody responseBody = mock(ResponseBody.class);
        String payload = IOUtils.toString(TuleapApiClientTest.class.getResourceAsStream("project_usergroups_payload.json"), UTF_8);

        when(client.newCall(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.isSuccessful()).thenReturn(true);
        when(response.body()).thenReturn(responseBody);
        when(responseBody.string()).thenReturn(payload);

        List<UserGroup> userGroups = tuleapApiClient.getProjectUserGroups(20, accessToken);

        assertEquals(8, userGroups.size());
    }

    @Test(expected = RuntimeException.class)
    public void itThrowsExceptionWhenTheCommitCannotBeRetrieved() throws IOException {
        TuleapAccessToken tuleapAccessToken = this.getTuleapAccessTokenStubClass();
        Call call = mock(Call.class);
        Response response = mock(Response.class);
        ResponseBody responseBody = mock(ResponseBody.class);

        when(client.newCall(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.isSuccessful()).thenReturn(false);

        tuleapApiClient.getCommit("10", "518151ezze", tuleapAccessToken);
    }

    @Test
    public void itReturnsTheWantedCommitInformation() throws IOException {
        Call call = mock(Call.class);
        Response response = mock(Response.class);
        ResponseBody responseBody = mock(ResponseBody.class);
        String jsonGitCommitPayload = IOUtils.toString(TuleapApiClientTest.class.getResourceAsStream("tuleap_git_commit_payload.json"), UTF_8.name());

        when(client.newCall(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.isSuccessful()).thenReturn(true);
        when(response.body()).thenReturn(responseBody);
        when(responseBody.string())
            .thenReturn(jsonGitCommitPayload);

        TuleapAccessToken accessToken = this.getTuleapAccessTokenStubClass();

        GitCommitEntity expectedGitCommit = new GitCommitEntity("38dfa09a67d7872d821b6a46eff340bc8ae0af0f", "2021-01-07T14:58:40+01:00");
        GitCommit gitCommit = tuleapApiClient.getCommit("4", "38dfa09a67d7872d821b6a46eff340bc8ae0af0f", accessToken);

        assertEquals(expectedGitCommit.getHash(), gitCommit.getHash());
        assertEquals(expectedGitCommit.getCommitDate(), gitCommit.getCommitDate());
    }

    @Test(expected = RuntimeException.class)
    public void itThrowsExceptionWhenTheTreePathCannotBeRetrieved() throws IOException, TreeNotFoundException {
        TuleapAccessToken tuleapAccessToken = this.getTuleapAccessTokenStubClass();
        Call call = mock(Call.class);
        Response response = mock(Response.class);

        when(client.newCall(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.code()).thenReturn(400);
        when(response.isSuccessful()).thenReturn(false);

        tuleapApiClient.getTree("10", "master", "some/unknown/path", tuleapAccessToken);
    }

    @Test(expected = TreeNotFoundException.class)
    public void itThrowsExceptionWhenTheTreePathIsNotFound() throws IOException, TreeNotFoundException {
        TuleapAccessToken tuleapAccessToken = this.getTuleapAccessTokenStubClass();
        Call call = mock(Call.class);
        Response response = mock(Response.class);

        when(client.newCall(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.code()).thenReturn(404);

        tuleapApiClient.getTree("10", "master", "some/unknown/path", tuleapAccessToken);

        verify(response, never()).isSuccessful();
    }

    @Test
    public void itReturnsTheWantedGitTreeInformation() throws IOException, TreeNotFoundException {
        Call call = mock(Call.class);
        Response response = mock(Response.class);
        ResponseBody responseBody = mock(ResponseBody.class);
        String jsonGitTreePayload = IOUtils.toString(TuleapApiClientTest.class.getResourceAsStream("tuleap_git_tree_payload.json"), UTF_8.name());

        when(client.newCall(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.code()).thenReturn(200);
        when(response.isSuccessful()).thenReturn(true);
        when(response.body()).thenReturn(responseBody);
        when(responseBody.string())
            .thenReturn(jsonGitTreePayload);

        TuleapAccessToken accessToken = this.getTuleapAccessTokenStubClass();

        GitTreeContent file1 = new GitTreeContentEntity("1c9c91a94db210c1f686dfd5d67f81813e02647b", "Jenkinsfile", "Jenkinsfile", "blob", "100644");
        GitTreeContent file2 = new GitTreeContentEntity("706530ef3efed3fe242033d0458c28707a19a3ec", "README", "README", "blob", "120000");
        GitTreeContent folder = new GitTreeContentEntity("699b379c93fbd7fc3c0b175ff7960ee9a475b1b6", "doc", "doc", "tree", "040000");

        List<GitTreeContent> expectedTreeContent = ImmutableList.of(file1, file2, folder);
        List<GitTreeContent> actualTreeContent = tuleapApiClient.getTree("4", "master", "", accessToken);

        assertEquals(expectedTreeContent.size(), actualTreeContent.size());

        expectedTreeContent.forEach( expectedContent -> {

            GitTreeContent content = actualTreeContent.stream()
                .filter(actualContent -> expectedContent.getId().equals(actualContent.getId()))
                .findFirst()
                .orElseThrow(RuntimeException::new);

            assertEquals(expectedContent.getName(), content.getName());
            assertEquals(expectedContent.getPath(), content.getPath());
            assertEquals(expectedContent.getMode(), content.getMode());
            assertEquals(expectedContent.getType(), content.getType());
        });
    }

    @Test(expected = RuntimeException.class)
    public void itThrowsExceptionWhenTheFilePathCannotBeRetrieved() throws IOException, FileContentNotFoundException {
        TuleapAccessToken tuleapAccessToken = this.getTuleapAccessTokenStubClass();
        Call call = mock(Call.class);
        Response response = mock(Response.class);

        when(client.newCall(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.code()).thenReturn(400);
        when(response.isSuccessful()).thenReturn(false);

        tuleapApiClient.getFileContent("10", "master", "some/unknown/path", tuleapAccessToken);
    }

    @Test(expected = FileContentNotFoundException.class)
    public void itThrowsExceptionWhenTheFileIsNotFound() throws IOException, FileContentNotFoundException {
        TuleapAccessToken tuleapAccessToken = this.getTuleapAccessTokenStubClass();
        Call call = mock(Call.class);
        Response response = mock(Response.class);

        when(client.newCall(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.code()).thenReturn(404);

        tuleapApiClient.getFileContent("10", "master", "some/unknown/path", tuleapAccessToken);

        verify(response, never()).isSuccessful();
    }

    @Test
    public void itReturnsTheWantedGitFileContentInformation() throws IOException, TreeNotFoundException, FileContentNotFoundException {
        Call call = mock(Call.class);
        Response response = mock(Response.class);
        ResponseBody responseBody = mock(ResponseBody.class);
        String jsonGitTreePayload = IOUtils.toString(TuleapApiClientTest.class.getResourceAsStream("tuleap_git_file_content_payload.json"), UTF_8.name());

        when(client.newCall(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.code()).thenReturn(200);
        when(response.isSuccessful()).thenReturn(true);
        when(response.body()).thenReturn(responseBody);
        when(responseBody.string())
            .thenReturn(jsonGitTreePayload);

        TuleapAccessToken accessToken = this.getTuleapAccessTokenStubClass();

        GitFileContent expectedFileContent = new GitFileContentEntity("base64", 10, "Naha", "TwoBro/Naha", "cGlwZWxpbmUgewog==");
        GitFileContent actualFileContent = tuleapApiClient.getFileContent("4", "master", "", accessToken);
        assertEquals(expectedFileContent.getContent(), actualFileContent.getContent());
        assertEquals(expectedFileContent.getEncoding(), actualFileContent.getEncoding());
        assertEquals(expectedFileContent.getName(), actualFileContent.getName());
        assertEquals(expectedFileContent.getPath(), actualFileContent.getPath());
        assertEquals(expectedFileContent.getSize(), actualFileContent.getSize());
    }


    private TuleapAccessToken getTuleapAccessTokenStubClass() {
        return new TuleapAccessToken() {
            @NotNull
            @Override
            public Secret getToken() {
                return Secret.fromString("my_t0k3n");
            }

            @NotNull
            @Override
            public Secret getPassword() {
                return Secret.fromString("d0lph1n");
            }

            @NotNull
            @Override
            public String getDescription() {
                return "";
            }

            @NotNull
            @Override
            public String getId() {
                return "";
            }

            @NotNull
            @Override
            public String getUsername() {
                return "Coco";
            }

            @Override
            public CredentialsScope getScope() {
                return CredentialsScope.SYSTEM;
            }

            @NotNull
            @Override
            public CredentialsDescriptor getDescriptor() {
                return new TuleapAccessTokenImpl.DescriptorImpl();
            }
        };
    }
}
