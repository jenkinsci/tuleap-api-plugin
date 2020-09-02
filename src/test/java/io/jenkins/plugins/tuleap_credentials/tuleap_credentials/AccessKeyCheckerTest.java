package io.jenkins.plugins.tuleap_credentials.tuleap_credentials;

import hudson.util.Secret;
import io.jenkins.plugins.tuleap_api.client.AccessKeyApi;
import io.jenkins.plugins.tuleap_api.client.AccessKeyScope;
import io.jenkins.plugins.tuleap_credentials.AccessKeyChecker;
import io.jenkins.plugins.tuleap_credentials.exceptions.InvalidAccessKeyException;
import io.jenkins.plugins.tuleap_credentials.exceptions.InvalidScopesForAccessKeyException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AccessKeyCheckerTest {
    @Mock private AccessKeyApi client;
    @InjectMocks private AccessKeyChecker accessKeyChecker;

    private Secret secret;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        secret = mock(Secret.class);
        when(secret.getPlainText()).thenReturn("SomeAccessKey");
    }

    @Test(expected = InvalidAccessKeyException.class)
    public void itShouldThrowAnExceptionIfKeyIsInvalid() throws InvalidScopesForAccessKeyException, InvalidAccessKeyException {
        when(client.checkAccessKeyIsValid(secret)).thenReturn(false);

        accessKeyChecker.verifyAccessKey(secret);
    }

    @Test(expected = InvalidScopesForAccessKeyException.class)
    public void itShouldThrowAnExceptionIfKeyDoesNotHaveTheNeededScopes() throws InvalidScopesForAccessKeyException, InvalidAccessKeyException {
        final AccessKeyScope scope = mock(AccessKeyScope.class);

        when(client.checkAccessKeyIsValid(secret)).thenReturn(true);
        when(scope.getIdentifier()).thenReturn("some:scope");
        when(client.getAccessKeyScopes(secret)).thenReturn(Collections.singletonList(scope));

        accessKeyChecker.verifyAccessKey(secret);
    }

    @Test
    public void itShouldNotGenerateAnyExceptionIfAccessKeyIsValid() throws InvalidScopesForAccessKeyException, InvalidAccessKeyException {
        final AccessKeyScope scope1 = mock(AccessKeyScope.class);
        final AccessKeyScope scope2 = mock(AccessKeyScope.class);

        when(client.checkAccessKeyIsValid(secret)).thenReturn(true);
        when(scope1.getIdentifier()).thenReturn("write:rest");
        when(scope2.getIdentifier()).thenReturn("write:git_repository");
        when(client.getAccessKeyScopes(secret)).thenReturn(Arrays.asList(scope1, scope2));

        accessKeyChecker.verifyAccessKey(secret);
    }
}
