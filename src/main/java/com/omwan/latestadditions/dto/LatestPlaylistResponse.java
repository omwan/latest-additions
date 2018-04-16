package com.omwan.latestadditions.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;

import java.util.List;

/**
 * DTO representing output of successfully creating/updating a "Latest Additions"
 * playlist containing the playlist URL and the first 10 tracks of the playlist.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LatestPlaylistResponse {

    private String playlistUrl;
    private List<PlaylistTrack> tracklistPreview;

    public String getPlaylistUrl() {
        return playlistUrl;
    }

    public void setPlaylistUrl(String playlistUrl) {
        this.playlistUrl = playlistUrl;
    }

    public List<PlaylistTrack> getTracklistPreview() {
        return tracklistPreview;
    }

    public void setTracklistPreview(List<PlaylistTrack> tracklistPreview) {
        this.tracklistPreview = tracklistPreview;
    }
}
