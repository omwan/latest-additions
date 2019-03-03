package com.omwan.latestadditions.mongo;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Document class to represent data in "userPlaylist" Mongo collection.
 */
@Document(collection = "userPlaylist")
public class UserPlaylist {

    private String userId;
    private String playlistId;

    public UserPlaylist() {
    }

    public UserPlaylist(String userId, String playlistId) {
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
}
