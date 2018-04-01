package com.omwan.latestadditions.component;

import com.omwan.latestadditions.dto.PlaylistUri;
import org.springframework.stereotype.Component;

@Component
public class UriComponent {

    public PlaylistUri buildPlaylistURI(String uri) {
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
