package com.omwan.latestadditions.component;

import com.omwan.latestadditions.dto.PlaylistURIWrapper;
import org.springframework.stereotype.Component;

@Component
public class UriComponent {

    public PlaylistURIWrapper buildPlaylistURI(String uri) {
        String expectedFormat = "spotify:user:(?s)(.*):playlist:(?s)(.*)";
        if (!uri.matches(expectedFormat)) {
            throw new IllegalArgumentException("Malformed playlist uri");
        }

        String[] values = uri.split(":");

        PlaylistURIWrapper playlistURIWrapper = new PlaylistURIWrapper();
        playlistURIWrapper.setUserId(values[2]);
        playlistURIWrapper.setPlaylistId(values[4]);

        return playlistURIWrapper;
    }
}
