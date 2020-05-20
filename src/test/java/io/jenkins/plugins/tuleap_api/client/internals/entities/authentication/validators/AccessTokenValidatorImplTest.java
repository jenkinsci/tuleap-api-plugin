package io.jenkins.plugins.tuleap_api.client.internals.entities.authentication.validators;

import io.jenkins.plugins.tuleap_api.client.internals.exceptions.InvalidHeaderException;
import okhttp3.CacheControl;
import okhttp3.Response;
import org.junit.Test;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class AccessTokenValidatorImplTest {

    @Test(expected = InvalidHeaderException.class)
    public void testResponseAccessTokenHeaderThrowsExceptionWhenBadCacheValue() throws InvalidHeaderException {
        Response response = mock(Response.class);

        CacheControl cache = mock(CacheControl.class);
        when(response.cacheControl()).thenReturn(cache);
        when(cache.noStore()).thenReturn(false);

        verify(response, never()).header("Pragma");

        AccessTokenValidatorImpl validator = new AccessTokenValidatorImpl();
        validator.validateAccessTokenHeader(response);
    }

    @Test(expected = InvalidHeaderException.class)
    public void testResponseAccessTokenHeaderThrowsExceptionWhenPragmaHeaderIsMissing() throws InvalidHeaderException {
        Response response = mock(Response.class);

        CacheControl cache = mock(CacheControl.class);
        when(response.cacheControl()).thenReturn(cache);
        when(cache.noStore()).thenReturn(true);

        when(response.header("Pragma")).thenReturn(null);

        AccessTokenValidatorImpl validator = new AccessTokenValidatorImpl();
        validator.validateAccessTokenHeader(response);
    }

    @Test(expected = InvalidHeaderException.class)
    public void testResponseAccessTokenHeaderThrowsExceptionWhenBadPragmaValue() throws InvalidHeaderException {
        Response response = mock(Response.class);
        when(response.header("Content-type")).thenReturn("application/json;charset=UTF-8");

        CacheControl cache = mock(CacheControl.class);
        when(response.cacheControl()).thenReturn(cache);
        when(cache.noStore()).thenReturn(true);

        when(response.header("Pragma")).thenReturn("issou");

        AccessTokenValidatorImpl validator = new AccessTokenValidatorImpl();
        validator.validateAccessTokenHeader(response);
    }

    @Test
    public void testResponseAccessTokenHeaderWhenAllChecksAreOk() throws InvalidHeaderException {
        Response response = mock(Response.class);
        when(response.header("Content-type")).thenReturn("application/json;charset=UTF-8");

        CacheControl cache = mock(CacheControl.class);
        when(response.cacheControl()).thenReturn(cache);
        when(cache.noStore()).thenReturn(true);

        when(response.header("Pragma")).thenReturn("no-cache");

        AccessTokenValidatorImpl validator = new AccessTokenValidatorImpl();
        validator.validateAccessTokenHeader(response);
    }

}
