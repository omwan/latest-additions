package com.omwan.latestadditions.controller;

import com.omwan.latestadditions.service.SavedPlaylistService;
import com.wrapper.spotify.model_objects.specification.Playlist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for saved playlist-related services.
 */
@RestController
@RequestMapping("/api/saved")
public class SavedPlaylistController {

    @Autowired
    private SavedPlaylistService savedPlaylistService;

    @RequestMapping(method = RequestMethod.GET, value = "")
    public List<Playlist> getExistingPlaylists() {
        return savedPlaylistService.getExistingPlaylists();
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    public void deleteSavedPlaylist(@PathVariable(name = "id") String playlistId) {
        savedPlaylistService.deleteSavedPlaylist(playlistId);
    }

}
