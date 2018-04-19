package com.omwan.latestadditions.controller;

import com.omwan.latestadditions.dto.BuildPlaylistRequest;
import com.omwan.latestadditions.dto.LatestPlaylistResponse;
import com.omwan.latestadditions.service.SpotifyPlaylistService;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for spotify playlist-related services.
 */
@RestController
@RequestMapping("/api/playlists")
public class SpotifyPlaylistController {

    @Autowired
    private SpotifyPlaylistService spotifyPlaylistService;

    @RequestMapping(method = RequestMethod.GET, value = "")
    public Paging<PlaylistSimplified> getUserPlaylists(@RequestParam(name = "limit", defaultValue = "40") int limit,
                                                       @RequestParam(name = "offset", defaultValue = "0") int offset) {
        return spotifyPlaylistService.getUserPlaylists(limit, offset);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{uri}")
    public Playlist getPlaylistDetails(@PathVariable(name = "uri") String playlistUri) {
        return spotifyPlaylistService.getPlaylistDetails(playlistUri);
    }

    @RequestMapping(method = RequestMethod.POST, value = "")
    public LatestPlaylistResponse buildLatestAdditionsPlaylist(@RequestBody BuildPlaylistRequest request) {
        return spotifyPlaylistService.buildLatestAdditionsPlaylist(request);
    }
}