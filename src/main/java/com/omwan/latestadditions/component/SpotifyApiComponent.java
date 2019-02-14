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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    private HttpServletResponse response;

    @Autowired
    private CookieUtils cookieUtils;

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
     * from cookies.
     *
     * @return SpotifyApi instance with access + refresh tokens
     */
    public SpotifyApi getApiWithTokens() {
        SpotifyApi spotifyApi = getSpotifyApi();

        String accessToken = cookieUtils.getCookieValue(request, "ACCESS_TOKEN");
        String refreshToken = cookieUtils.getCookieValue(request, "REFRESH_TOKEN");

        spotifyApi.setAccessToken(accessToken);
        spotifyApi.setRefreshToken(refreshToken);

        return spotifyApi;
    }

    /**
     * Helper to check if access and refresh tokens have already been set as cookies.
     *
     * @return whether or not access/refresh tokens exist as cookies.
     */
    public boolean tokensExist() {
        return WebUtils.getCookie(request, "ACCESS_TOKEN") != null
                && WebUtils.getCookie(request, "REFRESH_TOKEN") != null;
    }

    /**
     * Retrieve user ID of current user, and save as a cookie.
     *
     * @return user ID of current user
     */
    public String getCurrentUserId() {
        String userId = "";
        try {
            userId = cookieUtils.getCookieValue(request, "USER_ID");
            return userId;
        } catch (RuntimeException e) {
            userId = getCurrentUserIdFromApi();
            response.addCookie(cookieUtils.buildCookie("USER_ID", userId));
            return userId;
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
        spotifyApi.setRefreshToken(cookieUtils.getCookieValue(request, "REFRESH_TOKEN"));

        try {
            AuthorizationCodeCredentials authorizationCodeCredentials = spotifyApi.authorizationCodeRefresh()
                    .build()
                    .execute();

            response.addCookie(cookieUtils.buildCookie("ACCESS_TOKEN",
                    authorizationCodeCredentials.getAccessToken()));
            response.addCookie(cookieUtils.buildCookie("REFRESH_TOKEN",
                    authorizationCodeCredentials.getRefreshToken()));

            System.out.println(String.format("Token successfully refreshed, expires in %s",
                    authorizationCodeCredentials.getExpiresIn()));
        } catch (IOException | SpotifyWebApiException e) {
            throw new RuntimeException("Unable to refresh access token", e);
        }
    }
}
