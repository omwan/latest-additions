package com.omwan.latestadditions.spotify;

import com.omwan.latestadditions.dto.BuildPlaylistRequest;
import com.omwan.latestadditions.dto.LatestPlaylistResponse;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for playlist-related services.
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
    public Playlist getPlaylistDetails(@RequestParam(name = "uri") String playlistUri) {
        return playlistService.getPlaylistDetails(playlistUri);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/existing")
    public List<Playlist> getExistingPlaylists() {
        return playlistService.getExistingPlaylists();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/build")
    public LatestPlaylistResponse buildLatestAdditionsPlaylist(@RequestBody BuildPlaylistRequest request) {
        return playlistService.buildLatestAdditionsPlaylist(request);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/existing")
    public void deleteSavedPlaylist(@RequestParam(name = "uri") String playlistUri) {
        playlistService.deleteSavedPlaylist(playlistUri);
    }
}