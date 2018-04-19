package com.omwan.latestadditions.service;

import com.wrapper.spotify.model_objects.specification.Playlist;

import java.util.List;

/**
 * Services pertaining to playlist info saved to MongoDB.
 */
public interface SavedPlaylistService {

    List<Playlist> getExistingPlaylists();

    void deleteSavedPlaylist(String playlistUri);
}
