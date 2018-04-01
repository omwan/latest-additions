package com.omwan.latestadditions.spotify;

import com.omwan.latestadditions.dto.BuildPlaylistRequest;
import com.omwan.latestadditions.dto.PlaylistUri;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for application.
 */
@RestController
@RequestMapping("/api/playlist")
public class PlaylistController {

    @Autowired
    private PlaylistService playlistService;

    @RequestMapping(method = RequestMethod.GET, value = "/userplaylists")
    public Paging<PlaylistSimplified> getUserPlaylists(@RequestParam(name = "limit", defaultValue = "40") int limit,
                                                       @RequestParam(name = "offset", defaultValue = "0") int offset) {
        return playlistService.getUserPlaylists(limit, offset);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/details")
    public Playlist getPlaylistDetails(@RequestParam(name = "uri") String playlistURI) {
        return playlistService.getPlaylistDetails(playlistURI);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/build")
    public PlaylistUri buildLatestAdditionsPlaylist(@RequestBody BuildPlaylistRequest request) {
        return playlistService.buildLatestAdditionsPlaylist(request);
    }
}