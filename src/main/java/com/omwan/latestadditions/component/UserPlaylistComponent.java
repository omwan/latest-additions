package com.omwan.latestadditions.component;

import com.omwan.latestadditions.dto.PlaylistUri;
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
     * @return list of playlist URIs.
     */
    public List<PlaylistUri> getPlaylistsForUser(String userId) {
        return userPlaylistRepository.findByUserId(userId).stream()
                .map(userPlaylist -> UriUtils.buildPlaylistUri(userPlaylist.getPlaylistUri()))
                .collect(Collectors.toList());
    }

    /**
     * Save a playlist URI for a given user.
     *
     * @param userId      user ID to save playlist for
     * @param playlistUri playlist to save
     */
    public UserPlaylist saveUserPlaylist(String userId, String playlistUri) {
        UserPlaylist userPlaylist = new UserPlaylist();
        userPlaylist.setUserId(userId);
        userPlaylist.setPlaylistUri(playlistUri);
        return userPlaylistRepository.save(userPlaylist);
    }

    /**
     * Delete the playlist with the given URI. If no playlists are found matching
     * the given URI, an exception is thrown.
     *
     * @param playlistUri URI of playlist to delete.
     */
    public void deleteSavedPlaylist(String playlistUri) {
        int deletedCount = userPlaylistRepository.deleteByPlaylistUri(playlistUri);
        if (deletedCount != 1) {
            throw new IllegalArgumentException("No saved playlists found with URI " + playlistUri);
        }
    }
}
