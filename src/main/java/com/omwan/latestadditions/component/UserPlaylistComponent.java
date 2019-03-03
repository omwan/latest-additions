package com.omwan.latestadditions.component;

import com.omwan.latestadditions.dto.PlaylistIdWrapper;
import com.omwan.latestadditions.mongo.UserPlaylist;
import com.omwan.latestadditions.mongo.UserPlaylistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Component to manage functionalities relating to UserPlaylist objects.
 */
@Component
public class UserPlaylistComponent {

    @Autowired
    private UserPlaylistRepository userPlaylistRepository;

    /**
     * Retrieve URIs of all saved playlists for a given user.
     *
     * @param userId user ID to retrieve playlists for
     * @return list of playlist wrapper objects.
     */
    public List<PlaylistIdWrapper> getPlaylistsForUser(String userId) {
        return userPlaylistRepository.findByUserId(userId).stream()
                .map(userPlaylist -> PlaylistUtils.buildPlaylistWrapper(userPlaylist.getPlaylistId(), userId))
                .collect(Collectors.toList());
    }

    /**
     * Save a playlist URI for a given user.
     *
     * @param userId     user ID to save playlist for
     * @param playlistId playlist to save
     */
    public UserPlaylist saveUserPlaylist(String userId, String playlistId) {
        UserPlaylist userPlaylist = new UserPlaylist();
        userPlaylist.setUserId(userId);
        userPlaylist.setPlaylistId(playlistId);
        return userPlaylistRepository.save(userPlaylist);
    }

    /**
     * Delete the playlist with the given URI. If no playlists are found matching
     * the given URI, an exception is thrown.
     *
     * @param playlistId ID of playlist to delete.
     */
    public void deleteSavedPlaylist(String playlistId) {
        int deletedCount = userPlaylistRepository.deleteByPlaylistId(playlistId);
        if (deletedCount != 1) {
            throw new IllegalArgumentException("No saved playlists found with ID " + playlistId);
        }
    }
}
