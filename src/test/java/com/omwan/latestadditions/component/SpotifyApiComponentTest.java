package com.omwan.latestadditions.component;

import com.wrapper.spotify.SpotifyApi;
import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.Tested;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for SpotifyApiComponent.
 */
public class SpotifyApiComponentTest {

    private final static String SPOTIFY_CLIENT = "client";
    private final static String SPOTIFY_CLIENT_SECRET = "secret";
    private final static String SPOTIFY_REDIRECT_URI = "redirect";

    @Tested
    private SpotifyApiComponent spotifyApiComponent;

    @Injectable
    private HttpServletRequest request;

    @Injectable
    private HttpServletResponse response;

    @Injectable
    private CookieUtils cookieUtils;

    @Before
    public void setup() {
        spotifyApiComponent = new SpotifyApiComponent();
        Deencapsulation.setField(spotifyApiComponent, "spotifyClient", SPOTIFY_CLIENT);
        Deencapsulation.setField(spotifyApiComponent, "spotifyClientSecret", SPOTIFY_CLIENT_SECRET);
        Deencapsulation.setField(spotifyApiComponent, "spotifyRedirectUri", SPOTIFY_REDIRECT_URI);
    }

    /**
     * Assert that a SpotifyApi instance can be successfully built with
     * the correct spotify client, secret, and redirect values.
     */
    @Test
    public void testGetSpotifyApi() {
        SpotifyApi actual = spotifyApiComponent.getSpotifyApi();
        assertEquals(actual.getClientId(), SPOTIFY_CLIENT);
        assertEquals(actual.getClientSecret(), SPOTIFY_CLIENT_SECRET);
        assertEquals(actual.getRedirectURI().toString(), SPOTIFY_REDIRECT_URI);
    }

    /**
     * Assert that the access and refresh tokens can be successfully retrieved
     * from the cookies and set in a spotify api instance.
     */
    @Test
    public void testGetApiWithTokens() {
        final String expectedAccessToken = "access token";
        final String expectedRefreshToken = "refresh token";

        new Expectations() {{
            cookieUtils.getCookieValue(request, "ACCESS_TOKEN");
            returns(expectedAccessToken);

            cookieUtils.getCookieValue(request, "REFRESH_TOKEN");
            returns(expectedRefreshToken);
        }};

        SpotifyApi actual = spotifyApiComponent.getApiWithTokens();
        assertEquals(actual.getClientId(), SPOTIFY_CLIENT);
        assertEquals(actual.getClientSecret(), SPOTIFY_CLIENT_SECRET);
        assertEquals(actual.getRedirectURI().toString(), SPOTIFY_REDIRECT_URI);
        assertEquals(actual.getAccessToken(), expectedAccessToken);
        assertEquals(actual.getRefreshToken(), expectedRefreshToken);
    }

    /**
     * Assert that the current user's ID can be retrieved from the cookie.
     */
    @Test
    public void testGetCurrentUserIdWithCookie() {
        final String expectedUserId = "user ID";

        new Expectations() {{
            cookieUtils.getCookieValue(request, "USER_ID");
            returns(expectedUserId);
        }};

        String actual = spotifyApiComponent.getCurrentUserId();
        assertEquals(actual, expectedUserId);
    }

    /**
     * Assert that if the current user ID has not yet been set as a cookie,
     * it can be retrieved from the API and then is set as a cookie.
     */
    @Test
    public void testGetCurrentUserIdWithoutCookie() throws Exception {
        final String expectedUserId = "user ID";

        new MockUp<SpotifyApiComponent>() {
            @Mock
            public String getCurrentUserIdFromApi() {
                return expectedUserId;
            }
        };

        final Cookie userIdCookie = new Cookie("USER_ID", expectedUserId);

        new Expectations() {{
            cookieUtils.getCookieValue(request, "USER_ID");
            result = new RuntimeException();

            cookieUtils.buildCookie("USER_ID", expectedUserId);
            returns(userIdCookie);

            response.addCookie(userIdCookie);
        }};

        String actual = spotifyApiComponent.getCurrentUserId();
        assertEquals(actual, expectedUserId);
    }
}