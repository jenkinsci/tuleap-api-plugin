package io.jenkins.plugins.tuleap_api.client.internals.entities.authentication.validators;

import io.jenkins.plugins.tuleap_api.client.internals.exceptions.InvalidHeaderException;
import okhttp3.Response;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class HeaderAuthenticationCheckerImplTest {

    @Test(expected = InvalidHeaderException.class)
    public void testValidateHeaderThrowsExceptionWhenThereIsNoContentType() throws InvalidHeaderException {
        Response response = mock(Response.class);
        when(response.header("Content-type")).thenReturn(null);
        verify(response, never()).cacheResponse();

        HeaderAuthenticationValidatorImpl headerValidator = new HeaderAuthenticationValidatorImpl();
        headerValidator.validateHeader(response);
    }

    @Test(expected = InvalidHeaderException.class)
    public void testValidateHeaderThrowsExceptionWhenBadContentType() throws InvalidHeaderException {
        Response response = mock(Response.class);
        when(response.header("Content-type")).thenReturn("application/ogg");
        verify(response, never()).cacheResponse();

        HeaderAuthenticationValidatorImpl headerValidator = new HeaderAuthenticationValidatorImpl();
        headerValidator.validateHeader(response);
    }

    @Test
    public void testValidateHeaderIsOk() throws InvalidHeaderException {
        Response response = mock(Response.class);
        when(response.header("Content-type")).thenReturn("application/json;charset=utf-8");
        verify(response, never()).cacheResponse();

        HeaderAuthenticationValidatorImpl headerValidator = new HeaderAuthenticationValidatorImpl();
        headerValidator.validateHeader(response);
    }
}
