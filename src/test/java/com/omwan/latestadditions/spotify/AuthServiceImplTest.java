package com.omwan.latestadditions.spotify;

import com.omwan.latestadditions.component.SpotifyApiComponent;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.Tested;
import mockit.Verifications;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.URI;

/**
 * Unit tests for authorization services.
 */
public class AuthServiceImplTest {

    @Tested
    private AuthService authService;

    @Injectable
    private HttpSession httpSession;

    @Injectable
    private SpotifyApiComponent spotifyApiComponent;

    @Injectable
    private HttpServletResponse response;

    @Before
    public void setup() {
        authService = new AuthServiceImpl();
    }

    /**
     * Assert that the authorization URI can be successfully retrieved and
     * the response is redirected accordingly.
     *
     * @throws Exception
     */
    @Test
    public void testAuthorize() throws Exception {
        final String authUri = "auth";
        new MockUp<AuthorizationCodeUriRequest>() {
            @Mock
            public URI execute() {
                return URI.create(authUri);
            }
        };

        new Expectations() {{
            spotifyApiComponent.getSpotifyApi();
            returns(buildMockSpotifyApi());
        }};

        authService.authorize();

        new Verifications() {{
            response.sendRedirect(authUri);
        }};
    }

    /**
     * Assert that the access and refresh tokens can be successfully retrieved
     * and set as session attributes.
     */
    @Test
    public void testSetToken() throws Exception {
        final String token = "token";
        final String accessToken = "access token";
        final String refreshToken = "refresh token";
        new MockUp<AuthorizationCodeRequest>() {
            @Mock
            public AuthorizationCodeCredentials execute() {
                return buildMockedAuthCredentials(accessToken, refreshToken);
            }
        };

        new Expectations() {{
            spotifyApiComponent.getSpotifyApi();
            returns(buildMockSpotifyApi());

            response.sendRedirect(anyString);
        }};

        authService.setToken(token);

        new Verifications() {{
            httpSession.setAttribute("ACCESS_TOKEN", accessToken);
            httpSession.setAttribute("REFRESH_TOKEN", refreshToken);
        }};
    }

    /**
     * Assert that if an error occurs while retrieving the access and refresh
     * tokens, the appropriate exception is thrown.
     */
    @Test(expected = RuntimeException.class)
    public void testSetTokenSpotifyException() throws Exception {
        final String token = "token";
        new MockUp<AuthorizationCodeRequest>() {
            @Mock
            public AuthorizationCodeCredentials execute() throws Exception {
                throw new SpotifyWebApiException("spotify exception");
            }
        };

        new Expectations() {{
            spotifyApiComponent.getSpotifyApi();
            returns(buildMockSpotifyApi());
        }};

        authService.setToken(token);
    }

    /**
     * Create a mocked instance of a SpotifyApi object.
     *
     * @return mocked SpotifyApi object.
     */
    private SpotifyApi buildMockSpotifyApi() {
        return new SpotifyApi.Builder()
                .setClientId("client")
                .setClientSecret("secret")
                .setRedirectUri(URI.create("redirect"))
                .build();
    }

    /**
     * Created a mocked instance of an AuthorizationCodeCredentials object
     * with the given parameters.
     *
     * @param accessToken  mock access token
     * @param refreshToken mock refresh token
     * @return mocked AuthorizationCodeCredentials object
     */
    private AuthorizationCodeCredentials buildMockedAuthCredentials(String accessToken,
                                                                    String refreshToken) {
        return new AuthorizationCodeCredentials.Builder()
                .setAccessToken(accessToken)
                .setRefreshToken(refreshToken)
                .build();
    }

}