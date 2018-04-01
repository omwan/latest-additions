package com.omwan.latestadditions.mongo;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Document class to represent data in "userPlaylist" Mongo collection.
 */
@Document(collection = "userPlaylist")
public class UserPlaylist {

    private String userId;
    private String playlistUri;

    public UserPlaylist() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPlaylistUri() {
        return playlistUri;
    }

    public void setPlaylistUri(String playlistUri) {
        this.playlistUri = playlistUri;
    }
}
