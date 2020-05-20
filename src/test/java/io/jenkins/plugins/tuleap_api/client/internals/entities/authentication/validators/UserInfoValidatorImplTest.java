package io.jenkins.plugins.tuleap_api.client.internals.entities.authentication.validators;

import io.jenkins.plugins.tuleap_api.client.authentication.UserInfo;
import io.jenkins.plugins.tuleap_api.client.internals.exceptions.InvalidHeaderException;
import okhttp3.Handshake;
import okhttp3.Response;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserInfoValidatorImplTest {
    @Test(expected = InvalidHeaderException.class)
    public void testItThrowsExceptionIfTheConnectionDoesNotUseTLS() throws InvalidHeaderException {
        Response response = mock(Response.class);
        when(response.handshake()).thenReturn(null);

        UserInfoValidatorImpl userInfoValidator = new UserInfoValidatorImpl();
        userInfoValidator.validateUserInfoHandshake(response);
    }

    @Test
    public void testIsOkWhenTheConnectionUsesTLS() throws InvalidHeaderException {
        Response response = mock(Response.class);
        Handshake handshake = mock(Handshake.class);
        when(response.handshake()).thenReturn(handshake);

        UserInfoValidatorImpl userInfoValidator = new UserInfoValidatorImpl();
        userInfoValidator.validateUserInfoHandshake(response);
    }

    @Test(expected = InvalidHeaderException.class)
    public void testThrowsExceptionWhenTheSubjectClaimIsNull() throws InvalidHeaderException {
        UserInfo userInfo = mock(UserInfo.class);
        when(userInfo.getSubject()).thenReturn(null);

        UserInfoValidatorImpl userInfoValidator = new UserInfoValidatorImpl();
        userInfoValidator.validateUserInfoResponseBody(userInfo);
    }

    @Test
    public void testIsOkWhenTheSubjectClaimIsNotNull() throws InvalidHeaderException {
        UserInfo userInfo = mock(UserInfo.class);
        when(userInfo.getSubject()).thenReturn("105");

        UserInfoValidatorImpl userInfoValidator = new UserInfoValidatorImpl();
        userInfoValidator.validateUserInfoResponseBody(userInfo);
    }
}
