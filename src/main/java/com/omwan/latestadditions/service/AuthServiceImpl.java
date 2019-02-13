package com.omwan.latestadditions.service;

import com.omwan.latestadditions.component.SpotifyApiComponent;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

/**
 * Implementation of services relating to authentication/authorization.
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private SpotifyApiComponent spotifyApiComponent;

    @Value("${cookie.domain}")
    private String cookieDomain;

    /**
     * Make authorization request for API usage.
     */
    @Override
    public void authorize(HttpServletResponse response) {
        List<String> scopes = Arrays.asList(
                "playlist-read-private",
                "playlist-modify-private",
                "playlist-modify-public",
                "playlist-read-collaborative");

        SpotifyApi spotifyApi = spotifyApiComponent.getSpotifyApi();
        AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
                .scope(String.join(", ", scopes))
                .show_dialog(true)
                .build();
        URI authUri = authorizationCodeUriRequest.execute();
        handleRedirect(response, authUri.toString(), "Could not redirect to permissions page");
    }

    /**
     * Set the access token for the spotifyApi instance as given by the redirect.
     *
     * @param token access token
     */
    @Override
    public void setToken(String token, HttpServletResponse response) {
        SpotifyApi spotifyApi = spotifyApiComponent.getSpotifyApi();
        AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(token)
                .build();
        try {
            AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();
            response.addCookie(buildCookie("ACCESS_TOKEN", authorizationCodeCredentials.getAccessToken()));
            response.addCookie(buildCookie("REFRESH_TOKEN", authorizationCodeCredentials.getRefreshToken()));
            handleRedirect(response, "/", "Could not redirect to application main page");
        } catch (IOException | SpotifyWebApiException e) {
            throw new RuntimeException("Could not retrieve auth code credentials", e);
        }
    }

    private Cookie buildCookie(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setDomain(cookieDomain);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        return cookie;
    }

    /**
     * Helper to send a redirect to a given URL.
     *
     * @param url          address to send redirect to
     * @param errorMessage error message to return if redirect fails
     */
    private void handleRedirect(HttpServletResponse response, String url, String errorMessage) {
        try {
            response.sendRedirect(url);
        } catch (IOException e) {
            throw new RuntimeException(errorMessage, e);
        }
    }
}
