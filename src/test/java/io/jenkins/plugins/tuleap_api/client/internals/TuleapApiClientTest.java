package io.jenkins.plugins.tuleap_api.client.internals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.collect.ImmutableList;
import hudson.util.Secret;
import io.jenkins.plugins.tuleap_api.client.UserGroup;
import io.jenkins.plugins.tuleap_api.client.authentication.AccessToken;
import io.jenkins.plugins.tuleap_api.client.internals.entities.ProjectEntity;
import io.jenkins.plugins.tuleap_api.client.internals.entities.UserGroupEntity;
import io.jenkins.plugins.tuleap_server_configuration.TuleapConfiguration;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
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
        mapper = new ObjectMapper().registerModule(new GuavaModule());
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

        UserGroup userGroup1 = new UserGroupEntity("project_members", new ProjectEntity("coincoin"));
        UserGroup userGroup2 = new UserGroupEntity("project_admins", new ProjectEntity("coincoin"));
        UserGroup userGroup3 = new UserGroupEntity("project_members", new ProjectEntity("git-test"));

        List<UserGroup> expectedList = ImmutableList.of(userGroup1, userGroup2, userGroup3);

        ImmutableList<UserGroup> resultList = tuleapApiClient.getUserMembershipName(accessToken);
        assertEquals(resultList.get(0).getGroupName(), expectedList.get(0).getGroupName());
        assertEquals(resultList.get(0).getProjectName(), expectedList.get(0).getProjectName());

        assertEquals(resultList.get(1).getGroupName(), expectedList.get(1).getGroupName());
        assertEquals(resultList.get(1).getProjectName(), expectedList.get(1).getProjectName());

        assertEquals(resultList.get(2).getGroupName(), expectedList.get(2).getGroupName());
        assertEquals(resultList.get(2).getProjectName(), expectedList.get(2).getProjectName());
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

        UserGroup expectedUserGroup = new UserGroupEntity("project_members", new ProjectEntity("coincoin"));
        UserGroup resultUserGroup = tuleapApiClient.getUserGroup("106_3", accessToken);
        assertEquals(expectedUserGroup.getGroupName(), resultUserGroup.getGroupName());
        assertEquals(expectedUserGroup.getProjectName(), resultUserGroup.getProjectName());
    }
}
