package io.jenkins.plugins.tuleap_api.client.internals.entities.authentication.checks;

import io.jenkins.plugins.tuleap_api.client.internals.exceptions.MalformedHeaderException;
import okhttp3.CacheControl;
import okhttp3.Handshake;
import okhttp3.Response;
import org.junit.Test;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class HeaderAuthenticationCheckerImplTest {

    @Test(expected = MalformedHeaderException.class)
    public void testResponseAccessTokenHeaderThrowsExceptionWhenThereIsNoContentType() throws MalformedHeaderException {
        Response response = mock(Response.class);
        when(response.header("Content-type")).thenReturn(null);
        verify(response, never()).cacheResponse();

        HeaderAuthenticationCheckerImpl accessTokenChecker = new HeaderAuthenticationCheckerImpl();
        accessTokenChecker.checkAccessTokenHeader(response);
    }

    @Test(expected = MalformedHeaderException.class)
    public void testResponseAccessTokenHeaderThrowsExceptionWhenBadContentType() throws MalformedHeaderException {
        Response response = mock(Response.class);
        when(response.header("Content-type")).thenReturn("application/ogg");
        verify(response, never()).cacheResponse();

        HeaderAuthenticationCheckerImpl accessTokenChecker = new HeaderAuthenticationCheckerImpl();
        accessTokenChecker.checkAccessTokenHeader(response);
    }

    @Test(expected = MalformedHeaderException.class)
    public void testResponseAccessTokenHeaderThrowsExceptionWhenBadCacheValue() throws MalformedHeaderException {
        Response response = mock(Response.class);
        when(response.header("Content-type")).thenReturn("application/json;charset=UTF-8");

        CacheControl cache = mock(CacheControl.class);
        when(response.cacheControl()).thenReturn(cache);
        when(cache.noStore()).thenReturn(false);

        verify(response, never()).header("Pragma");

        HeaderAuthenticationCheckerImpl accessTokenChecker = new HeaderAuthenticationCheckerImpl();
        accessTokenChecker.checkAccessTokenHeader(response);
    }

    @Test(expected = MalformedHeaderException.class)
    public void testResponseAccessTokenHeaderThrowsExceptionWhenPragmaHeaderIsMissing() throws MalformedHeaderException {
        Response response = mock(Response.class);
        when(response.header("Content-type")).thenReturn("application/json;charset=UTF-8");

        CacheControl cache = mock(CacheControl.class);
        when(response.cacheControl()).thenReturn(cache);
        when(cache.noStore()).thenReturn(true);

        when(response.header("Pragma")).thenReturn(null);

        HeaderAuthenticationCheckerImpl accessTokenChecker = new HeaderAuthenticationCheckerImpl();
        accessTokenChecker.checkAccessTokenHeader(response);
    }

    @Test(expected = MalformedHeaderException.class)
    public void testResponseAccessTokenHeaderThrowsExceptionWhenBadPragmaValue() throws MalformedHeaderException {
        Response response = mock(Response.class);
        when(response.header("Content-type")).thenReturn("application/json;charset=UTF-8");

        CacheControl cache = mock(CacheControl.class);
        when(response.cacheControl()).thenReturn(cache);
        when(cache.noStore()).thenReturn(true);

        when(response.header("Pragma")).thenReturn("issou");

        HeaderAuthenticationCheckerImpl accessTokenChecker = new HeaderAuthenticationCheckerImpl();
        accessTokenChecker.checkAccessTokenHeader(response);
    }

    @Test
    public void testResponseAccessTokenHeaderWhenAllChecksAreOk() throws MalformedHeaderException {
        Response response = mock(Response.class);
        when(response.header("Content-type")).thenReturn("application/json;charset=UTF-8");

        CacheControl cache = mock(CacheControl.class);
        when(response.cacheControl()).thenReturn(cache);
        when(cache.noStore()).thenReturn(true);

        when(response.header("Pragma")).thenReturn("no-cache");

        HeaderAuthenticationCheckerImpl accessTokenChecker = new HeaderAuthenticationCheckerImpl();
        accessTokenChecker.checkAccessTokenHeader(response);
    }

    @Test(expected = MalformedHeaderException.class)
    public void testItThrowsExceptionIfTheConnectionDoesNotUseTLS() throws MalformedHeaderException {
        Response response = mock(Response.class);
        when(response.handshake()).thenReturn(null);

        HeaderAuthenticationCheckerImpl userInfoChecker = new HeaderAuthenticationCheckerImpl();
        userInfoChecker.checkUserInfoHandshake(response);
    }

    @Test
    public void testIsOkWhenTheConnectionUsesTLS() throws MalformedHeaderException {
        Response response = mock(Response.class);
        Handshake handshake = mock(Handshake.class);
        when(response.handshake()).thenReturn(handshake);

        HeaderAuthenticationCheckerImpl userInfoChecker = new HeaderAuthenticationCheckerImpl();
        userInfoChecker.checkUserInfoHandshake(response);
    }

    @Test(expected = MalformedHeaderException.class)
    public void testResponseHeaderThrowsExceptionWhenTheContentTypeIsMissing() throws MalformedHeaderException {
        Response response = mock(Response.class);
        when(response.header("Content-type")).thenReturn(null);

        HeaderAuthenticationCheckerImpl userInfoChecker = new HeaderAuthenticationCheckerImpl();
        userInfoChecker.checkResponseHeader(response);
    }

    @Test(expected = MalformedHeaderException.class)
    public void testUserResponseHeaderThrowsExceptionWhenTheContentTypeValueIsNotExpected() throws MalformedHeaderException {
        Response response = mock(Response.class);
        when(response.header("Content-type")).thenReturn("multipart/form-data; boundary=something");

        HeaderAuthenticationCheckerImpl userInfoChecker = new HeaderAuthenticationCheckerImpl();
        userInfoChecker.checkResponseHeader(response);
    }

    @Test
    public void testUserInfoWhenGoodContentType() throws MalformedHeaderException {
        Response response = mock(Response.class);
        when(response.header("Content-type")).thenReturn("application/json;charset=utf-8");

        HeaderAuthenticationCheckerImpl userInfoChecker = new HeaderAuthenticationCheckerImpl();
        userInfoChecker.checkResponseHeader(response);
    }
}
