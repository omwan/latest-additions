package com.omwan.latestadditions.spotify;

import com.omwan.latestadditions.dto.BuildPlaylistRequest;
import com.omwan.latestadditions.dto.PlaylistUri;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;

import java.util.List;

/**
 * Services pertaining to playlists.
 */
public interface PlaylistService {

    Paging<PlaylistSimplified> getUserPlaylists(int limit, int offset);

    Playlist getPlaylistDetails(String playlistURI);

    List<Playlist> getExistingPlaylists();

    PlaylistUri buildLatestAdditionsPlaylist(BuildPlaylistRequest request);

    void deleteSavedPlaylist(String playlistUri);
}
