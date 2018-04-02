package com.omwan.latestadditions.component;

import com.omwan.latestadditions.dto.PlaylistUri;
import org.springframework.stereotype.Component;

/**
 * Component to manage functionalities relating to Uri wrappers.
 */
@Component
public class UriComponent {

    /**
     * Build a PlaylistUri instance from a playlist URI string.
     *
     * @param uri playlist URI string
     * @return PlaylistUri instance
     */
    public PlaylistUri buildPlaylistUri(String uri) {
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
