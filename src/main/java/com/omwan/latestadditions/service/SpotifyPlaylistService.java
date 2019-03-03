package com.omwan.latestadditions.service;

import com.omwan.latestadditions.dto.BuildPlaylistRequest;
import com.omwan.latestadditions.dto.LatestPlaylistResponse;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;

/**
 * Services pertaining to spotify playlists.
 */
public interface SpotifyPlaylistService {

    Paging<PlaylistSimplified> getUserPlaylists(int limit, int offset);

    Playlist getPlaylistDetails(String playlistId);

    LatestPlaylistResponse buildLatestAdditionsPlaylist(BuildPlaylistRequest request);
}
