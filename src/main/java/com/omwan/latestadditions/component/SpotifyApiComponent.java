package com.omwan.latestadditions.component;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.exceptions.detailed.UnauthorizedException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.model_objects.specification.User;
import com.wrapper.spotify.requests.data.AbstractDataRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URI;

/**
 * Component to manage common functionalities relating to the Spotify API.
 */
@Component
public class SpotifyApiComponent {

    @Value("${spotify.client}")
    private String spotifyClient;

    @Value("${spotify.client.secret}")
    private String spotifyClientSecret;

    @Value("${spotify.redirect.uri}")
    private String spotifyRedirectUri;

    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private HttpServletRequest request;

    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private HttpSession session;

    /**
     * Build instance of SpotifyApi with spotify client, secret, and redirect
     * from environment variables.
     *
     * @return SpotifyApi instance.
     */
    public SpotifyApi getSpotifyApi() {
        return new SpotifyApi.Builder()
                .setClientId(spotifyClient)
                .setClientSecret(spotifyClientSecret)
                .setRedirectUri(URI.create(spotifyRedirectUri))
                .build();
    }

    /**
     * Build instance of SpotifyApi with access and refresh tokens set
     * from session attributes.
     *
     * @return SpotifyApi instance with access + refresh tokens
     */
    public SpotifyApi getApiWithTokens() {
        SpotifyApi spotifyApi = getSpotifyApi();

        String accessToken = getCookieValue("ACCESS_TOKEN");
        String refreshToken = getCookieValue("REFRESH_TOKEN");

        spotifyApi.setAccessToken(accessToken);
        spotifyApi.setRefreshToken(refreshToken);

        return spotifyApi;
    }

    /**
     * Retrieve the value of the given cookie name, or throw an appropriate exception
     * if the cookie does not exist.
     *
     * @param cookieName name of cookie to retrieve value for
     * @return value of cookie
     */
    private String getCookieValue(String cookieName) {
        Cookie cookie = WebUtils.getCookie(request, cookieName);
        if (cookie == null) {
            throw new RuntimeException("Cookie " + cookieName + " does not exist");
        }
        return cookie.getValue();
    }

    /**
     * Helper to check if access and refresh tokens have already been set as
     * session attributes.
     *
     * @return whether or not access/refresh tokens exist as session attributes.
     */
    public boolean tokensExist() {
        return WebUtils.getCookie(request, "ACCESS_TOKEN") != null
                && WebUtils.getCookie(request, "REFRESH_TOKEN") != null;
    }

    /**
     * Retrieve user ID of current user, and save as a session attribute.
     *
     * @return user ID of current user
     */
    public String getCurrentUserId() {
        Object userIdAttribute = session.getAttribute("USER_ID");
        if (userIdAttribute == null) {
            String userId = getCurrentUserIdFromApi();
            session.setAttribute("USER_ID", userId);
            return userId;
        } else {
            return userIdAttribute.toString();
        }
    }

    /**
     * Make Spotify API call to retrieve user ID of current user.
     *
     * @return current user ID
     */
    private String getCurrentUserIdFromApi() {
        SpotifyApi spotifyApi = getApiWithTokens();
        AbstractDataRequest userRequest = spotifyApi.getCurrentUsersProfile()
                .build();
        User user = executeRequest(userRequest, "ugh");
        return user.getId();
    }

    /**
     * Helper to execute API requests.
     *
     * @param requestBuilder request to execute
     * @param errorMessage   error message to log in event of failure
     * @param <T>            return type of request
     * @return value of executed request
     */
    public <T> T executeRequest(AbstractDataRequest requestBuilder,
                                String errorMessage) {
        try {
            return requestBuilder.execute();
        } catch (UnauthorizedException e) {
            refreshToken();
            throw new RuntimeException("Token refreshed, try making request again");
        } catch (IOException | SpotifyWebApiException e) {
            throw new RuntimeException(errorMessage, e);
        }
    }

    /**
     * Refresh access token for spotify api.
     */
    private void refreshToken() {
        SpotifyApi spotifyApi = getSpotifyApi();
        spotifyApi.setRefreshToken(session.getAttribute("REFRESH_TOKEN").toString());

        try {
            AuthorizationCodeCredentials authorizationCodeCredentials = spotifyApi.authorizationCodeRefresh()
                    .build()
                    .execute();

            session.setAttribute("ACCESS_TOKEN", authorizationCodeCredentials.getAccessToken());
            session.setAttribute("REFRESH_TOKEN", authorizationCodeCredentials.getAccessToken());

            System.out.println(String.format("Token successfully refreshed, expires in %s",
                    authorizationCodeCredentials.getExpiresIn()));
        } catch (IOException | SpotifyWebApiException e) {
            throw new RuntimeException("Unable to refresh access token", e);
        }
    }
}
