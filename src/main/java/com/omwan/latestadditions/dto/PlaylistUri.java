package com.omwan.latestadditions.dto;

/**
 * Wrapper class for playlist URI strings.
 */
public class PlaylistUri {

    private String userId;
    private String playlistId;

    private int skipCount;
    private int offset;

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
