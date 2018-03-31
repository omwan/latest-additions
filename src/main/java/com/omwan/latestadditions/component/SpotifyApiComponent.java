package com.omwan.latestadditions.component;

import com.wrapper.spotify.SpotifyApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public class SpotifyApiComponent {

    @Value("${spotify.client}")
    private String spotifyClient;

    @Value("${spotify.client.secret}")
    private String spotifyClientSecret;

    @Value("${spotify.redirect.uri}")
    private String spotifyRedirectURI;

    public SpotifyApi getSpotifyApi() {
        return new SpotifyApi.Builder()
                .setClientId(spotifyClient)
                .setClientSecret(spotifyClientSecret)
                .setRedirectUri(URI.create(spotifyRedirectURI))
                .build();
    }
}
