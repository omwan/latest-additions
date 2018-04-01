package com.omwan.latestadditions.component;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.exceptions.detailed.UnauthorizedException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.requests.data.AbstractDataRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URI;

@Component
public class SpotifyApiComponent {

    @Value("${spotify.client}")
    private String spotifyClient;

    @Value("${spotify.client.secret}")
    private String spotifyClientSecret;

    @Value("${spotify.redirect.uri}")
    private String spotifyRedirectURI;

    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private HttpSession session;

    public SpotifyApi getSpotifyApi() {
        return new SpotifyApi.Builder()
                .setClientId(spotifyClient)
                .setClientSecret(spotifyClientSecret)
                .setRedirectUri(URI.create(spotifyRedirectURI))
                .build();
    }

    public SpotifyApi getApiWithTokens() {
        SpotifyApi spotifyApi = getSpotifyApi();

        String accessToken = session.getAttribute("ACCESS_TOKEN").toString();
        String refreshToken = session.getAttribute("REFRESH_TOKEN").toString();

        spotifyApi.setAccessToken(accessToken);
        spotifyApi.setRefreshToken(refreshToken);

        return spotifyApi;
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
            try {
                return requestBuilder.execute();
            } catch (IOException | SpotifyWebApiException ex) {
                throw new RuntimeException(errorMessage, e);
            }
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
