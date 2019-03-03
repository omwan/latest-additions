package com.omwan.latestadditions.component;

import com.omwan.latestadditions.dto.PlaylistIdWrapper;

/**
 * Util class to manage functionalities relating to Uri wrappers.
 */
public class PlaylistUtils {

    /**
     * Build a PlaylistIdWrapper instance from a playlist URI string.
     *
     * @param playlistId playlist ID
     * @return PlaylistIdWrapper instance
     */
    public static PlaylistIdWrapper buildPlaylistWrapper(String playlistId, String userId) {

        PlaylistIdWrapper playlistIdWrapper = new PlaylistIdWrapper();
        playlistIdWrapper.setPlaylistId(playlistId);
        playlistIdWrapper.setUserId(userId);

        return playlistIdWrapper;
    }
}
