package com.omwan.latestadditions.dto;

public class PlaylistUri {

    private String userId;
    private String playlistId;

    public PlaylistUri() {

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

    @Override
    public String toString() {
        return String.format("spotify:user:%s:playlist:%s", userId, playlistId);
    }
}
