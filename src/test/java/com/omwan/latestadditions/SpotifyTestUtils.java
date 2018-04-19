package com.omwan.latestadditions;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.model_objects.specification.Playlist;

import java.net.URI;

/**
 * Util methods to create mock objects for tests.
 */
public class SpotifyTestUtils {

    /**
     * Create a mocked instance of a spotify API with the necessary fields
     * set to make an API call.
     *
     * @return mocked spotify API object
     */
    public static SpotifyApi buildMockedSpotifyApi() {
        return new SpotifyApi.Builder()
                .setClientId("client")
                .setClientSecret("secret")
                .setRedirectUri(URI.create("redirect"))
                .setAccessToken("access token")
                .setRefreshToken("refresh token")
                .build();
    }

    /**
     * Create a mocked instance of a playlist with the given URI.
     *
     * @param playlistUri uri for playlist
     * @return mocked playlist
     */
    public static Playlist buildMockedPlaylist(String playlistUri) {
        return new Playlist.Builder()
                .setUri(playlistUri)
                .build();
    }
}
