package com.omwan.latestadditions.dto;

import io.swagger.models.auth.In;

import java.util.Map;

public class BuildPlaylistRequest {

    private Map<String, Integer> playlistUris;
    private boolean overwriteExisting;
    private String playlistName;
    private int numTracks;
    private String description;
    private boolean isPublic;
    private boolean isCollaborative;

    public Map<String, Integer> getPlaylistUris() {
        return playlistUris;
    }

    public void setPlaylistUris(Map<String, Integer> playlistUris) {
        this.playlistUris = playlistUris;
    }

    public boolean isOverwriteExisting() {
        return overwriteExisting;
    }

    public void setOverwriteExisting(boolean overwriteExisting) {
        this.overwriteExisting = overwriteExisting;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public int getNumTracks() {
        return numTracks;
    }

    public void setNumTracks(int numTracks) {
        this.numTracks = numTracks;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public boolean isCollaborative() {
        return isCollaborative;
    }

    public void setCollaborative(boolean collaborative) {
        isCollaborative = collaborative;
    }
}
