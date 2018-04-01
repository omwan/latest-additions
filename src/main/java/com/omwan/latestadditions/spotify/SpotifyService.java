package com.omwan.latestadditions.spotify;

import com.omwan.latestadditions.dto.BuildPlaylistRequest;
import com.omwan.latestadditions.dto.PlaylistUri;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;

import javax.servlet.http.HttpServletResponse;

public interface SpotifyService {

    void authorize(HttpServletResponse response);

    void setToken(String token, HttpServletResponse response);

    Paging<PlaylistSimplified> getUserPlaylists(int limit, int offset);

    Playlist getPlaylistDetails(String playlistURI);

    PlaylistUri buildLatestAdditionsPlaylist(BuildPlaylistRequest request);
}
