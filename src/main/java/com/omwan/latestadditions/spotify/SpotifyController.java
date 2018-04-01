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

import javax.servlet.http.HttpServletResponse;

/**
 * Controller for application.
 */
@RestController
@RequestMapping("/api/spotify")
public class SpotifyController {

    @Autowired
    private SpotifyService spotifyService;

    @RequestMapping(method = RequestMethod.GET, value = "/auth")
    public void authorize(HttpServletResponse response) {
        spotifyService.authorize(response);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/accesstoken")
    public void setToken(@RequestParam(name = "code") String token,
                         HttpServletResponse response) {
        spotifyService.setToken(token, response);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/playlists")
    public Paging<PlaylistSimplified> getUserPlaylists(@RequestParam(name = "limit", defaultValue = "40") int limit,
                                                       @RequestParam(name = "offset", defaultValue = "0") int offset) {
        return spotifyService.getUserPlaylists(limit, offset);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/playlistdetails")
    public Playlist getPlaylistDetails(@RequestParam(name = "uri") String playlistURI) {
        return spotifyService.getPlaylistDetails(playlistURI);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/playlists")
    public PlaylistUri buildLatestAdditionsPlaylist(@RequestBody BuildPlaylistRequest request) {
        return spotifyService.buildLatestAdditionsPlaylist(request);
    }
}