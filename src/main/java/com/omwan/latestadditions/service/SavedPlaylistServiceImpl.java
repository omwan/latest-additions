package com.omwan.latestadditions.service;

import com.omwan.latestadditions.component.SpotifyApiComponent;
import com.omwan.latestadditions.component.UserPlaylistComponent;
import com.omwan.latestadditions.dto.PlaylistUri;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.requests.data.AbstractDataRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of services pertaining to playlist info saved to MongoDB
 */
@Service
public class SavedPlaylistServiceImpl implements SavedPlaylistService {

    @Autowired
    private SpotifyApiComponent spotifyApiComponent;

    @Autowired
    private UserPlaylistComponent userPlaylistComponent;

    /**
     * Delete a previously saved playlist from application.
     *
     * @param playlistUri uri of playlist to delete
     */
    @Override
    public void deleteSavedPlaylist(String playlistUri) {
        userPlaylistComponent.deleteSavedPlaylist(playlistUri);
    }


    /**
     * Get all playlists that the current user has previously created
     * using this application.
     *
     * @return list of playlists
     */
    @Override
    public List<Playlist> getExistingPlaylists() {
        if (!spotifyApiComponent.tokensExist()) {
            return null;
        }

        String userId = spotifyApiComponent.getCurrentUserId();
        List<PlaylistUri> playlistsUris = userPlaylistComponent.getPlaylistsForUser(userId);

        List<Playlist> existingPlaylists = new ArrayList<>();

        for (PlaylistUri uri : playlistsUris) {
            SpotifyApi spotifyApi = spotifyApiComponent.getApiWithTokens();
            AbstractDataRequest getPlaylistRequest = spotifyApi.getPlaylist(userId, uri.getPlaylistId())
                    .fields("name,tracks(total),uri,id")
                    .build();
            String errorMessage = "Unable to retrieve existing playlists for user " + userId;
            Playlist playlist = spotifyApiComponent.executeRequest(getPlaylistRequest, errorMessage);
            existingPlaylists.add(playlist);
        }

        return existingPlaylists;
    }
}
