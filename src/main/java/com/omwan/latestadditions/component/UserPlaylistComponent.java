package com.omwan.latestadditions.component;

import com.omwan.latestadditions.db.UserPlaylistRepository;
import com.omwan.latestadditions.dto.PlaylistIdWrapper;
import com.omwan.latestadditions.db.UserPlaylist;
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
     * Retrieve IDs of all saved playlists for a given user.
     *
     * @param userId user ID to retrieve playlists for
     * @return list of playlist wrapper objects.
     */
    public List<PlaylistIdWrapper> getPlaylistsForUser(String userId) {
        return userPlaylistRepository.findByUserId(userId).stream()
                .map(userPlaylist -> new PlaylistIdWrapper(userPlaylist.getPlaylistId(), userId))
                .collect(Collectors.toList());
    }

    /**
     * Save a playlist for a given user.
     *
     * @param userId     user ID to save playlist for
     * @param playlistId ID of playlist to save
     */
    public void saveUserPlaylist(String userId, String playlistId) {
        UserPlaylist userPlaylist = new UserPlaylist();
        userPlaylist.setUserId(userId);
        userPlaylist.setPlaylistId(playlistId);
        userPlaylistRepository.save(userPlaylist);
    }

    /**
     * Delete the playlist with the given ID. If no playlists are found matching
     * the given ID, an exception is thrown.
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
