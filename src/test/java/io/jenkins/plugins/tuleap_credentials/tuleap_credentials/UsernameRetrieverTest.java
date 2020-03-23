package io.jenkins.plugins.tuleap_credentials.tuleap_credentials;

import hudson.util.Secret;
import io.jenkins.plugins.tuleap_api.client.User;
import io.jenkins.plugins.tuleap_api.client.UserApi;
import io.jenkins.plugins.tuleap_api.client.internals.entities.UserEntity;
import io.jenkins.plugins.tuleap_credentials.UsernameRetriever;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UsernameRetrieverTest {
    @Mock
    private UserApi client;

    @InjectMocks
    private UsernameRetriever usernameRetriever;

    private Secret secret;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        secret = mock(Secret.class);
        when(secret.getPlainText()).thenReturn("SomeAccessKey");
    }

    @Test
    public void itShouldReturnTheUsersUsername() {
        final String username = "mjagger";
        final User user = new UserEntity(username);

        when(client.getUserForAccessKey(secret)).thenReturn(user);

        assertEquals(username, usernameRetriever.getUsernameForToken(secret));
    }
}
