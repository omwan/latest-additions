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
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for SpotifyApiComponent.
 */
public class SpotifyApiComponentTest {

    @Tested
    private SpotifyApiComponent spotifyApiComponent;

    @Injectable
    private HttpServletRequest request;

    @Injectable
    private HttpSession httpSession;

    private final static String SPOTIFY_CLIENT = "client";
    private final static String SPOTIFY_CLIENT_SECRET = "secret";
    private final static String SPOTIFY_REDIRECT_URI = "redirect";

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
     * from the session attributes and set in a spotify api instance.
     */
    @Test
    public void testGetApiWithTokens() {
        final String expectedAccessToken = "access token";
        final String expectedRefreshToken = "refresh token";

        new MockUp<WebUtils>() {
            @Mock
            public Cookie getCookie(HttpServletRequest request, String cookieName) {
                if (cookieName.equals("ACCESS_TOKEN")) {
                    return new Cookie(cookieName, expectedAccessToken);
                } else {
                    return new Cookie(cookieName, expectedRefreshToken);
                }
            }
        };

        SpotifyApi actual = spotifyApiComponent.getApiWithTokens();
        assertEquals(actual.getClientId(), SPOTIFY_CLIENT);
        assertEquals(actual.getClientSecret(), SPOTIFY_CLIENT_SECRET);
        assertEquals(actual.getRedirectURI().toString(), SPOTIFY_REDIRECT_URI);
        assertEquals(actual.getAccessToken(), expectedAccessToken);
        assertEquals(actual.getRefreshToken(), expectedRefreshToken);
    }

    /**
     * Assert that if the access and refresh tokens cannot be retrieved from the
     * session attributes, the appropriate exception is thrown.
     */
    @Test(expected = RuntimeException.class)
    public void testGetApiWithTokensMissingAttributes() {
        new MockUp<WebUtils>() {
            @Mock
            public Cookie getCookie(HttpServletRequest request, String cookieName) {
                return null;
            }
        };

        spotifyApiComponent.getApiWithTokens();
    }

    /**
     * Assert that the current user's ID can be retrieved from the spotify API
     * and set as a session attribute.
     */
    @Test
    public void testGetCurrentUserIdNoSessionAttribute() {
        final String expectedUserId = "user ID";
        new MockUp<SpotifyApiComponent>() {
            @Mock
            public String getCurrentUserIdFromApi() {
                return expectedUserId;
            }
        };

        new Expectations() {{
            httpSession.getAttribute("USER_ID");
            returns(null);

            httpSession.setAttribute("USER_ID", expectedUserId);
        }};

        String actual = spotifyApiComponent.getCurrentUserId();
        assertEquals(actual, expectedUserId);
    }

    /**
     * Assert that the current user's ID can be retrieved from the session attributes.
     */
    @Test
    public void testGetCurrentUserIdWithSessionAttribute() {
        final String expectedUserId = "user ID";

        new Expectations() {{
            httpSession.getAttribute("USER_ID");
            returns(expectedUserId);
        }};

        String actual = spotifyApiComponent.getCurrentUserId();
        assertEquals(actual, expectedUserId);
    }

    /**
     * Mock a session attribute with the given value.
     *
     * @param value value of the session attribute to mock
     * @return mocked session attribute object
     */
    private MockUp<Object> mockSessionAttribute(String value) {
        return new MockUp<Object>() {
            @Mock
            public String toString() {
                return value;
            }
        };
    }
}