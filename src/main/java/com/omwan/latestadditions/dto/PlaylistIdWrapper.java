package com.omwan.latestadditions.dto;

/**
 * Wrapper class for playlist IDs and user info.
 */
public class PlaylistIdWrapper {

    private String userId;
    private String playlistId;

    private int skipCount;
    private int offset;

    public PlaylistIdWrapper() {

    }

    public PlaylistIdWrapper(String playlistId, String userId) {
        this.userId = userId;
        this.playlistId = playlistId;
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

    public int getSkipCount() {
        return skipCount;
    }

    public void setSkipCount(int skipCount) {
        this.skipCount = skipCount;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
