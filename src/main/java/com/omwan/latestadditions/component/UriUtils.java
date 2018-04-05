package com.omwan.latestadditions.component;

import com.omwan.latestadditions.dto.PlaylistUri;

/**
 * Util class to manage functionalities relating to Uri wrappers.
 */
public class UriUtils {

    /**
     * Build a PlaylistUri instance from a playlist URI string.
     *
     * @param uri playlist URI string
     * @return PlaylistUri instance
     */
    public static PlaylistUri buildPlaylistUri(String uri) {
        String expectedFormat = "spotify:user:(?s)(.*):playlist:(?s)(.*)";
        if (!uri.matches(expectedFormat)) {
            throw new IllegalArgumentException("Malformed playlist uri");
        }

        String[] values = uri.split(":");

        PlaylistUri playlistURIWrapper = new PlaylistUri();
        playlistURIWrapper.setUserId(values[2]);
        playlistURIWrapper.setPlaylistId(values[4]);

        return playlistURIWrapper;
    }
}
