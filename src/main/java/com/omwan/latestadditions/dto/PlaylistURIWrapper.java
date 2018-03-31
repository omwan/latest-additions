package com.omwan.latestadditions.dto;

public class PlaylistURIWrapper {

    private String userId;
    private String playlistId;

    public PlaylistURIWrapper() {

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }
}
