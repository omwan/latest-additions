package com.omwan.latestadditions.dto;

import java.util.Map;

/**
 * DTO representing specifications for creating/updating a "Latest Additions" playlist.
 */
public class BuildPlaylistRequest {

    private Map<String, Integer> playlistIds;
    private boolean overwriteExisting;
    private String playlistName;
    private int numTracks;
    private String description;
    private boolean isPublic;
    private boolean isCollaborative;
    private String playlistToOverwrite;

    public Map<String, Integer> getPlaylistIds() {
        return playlistIds;
    }

    public void setPlaylistIds(Map<String, Integer> playlistIds) {
        this.playlistIds = playlistIds;
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

    public String getPlaylistToOverwrite() {
        return playlistToOverwrite;
    }

    public void setPlaylistToOverwrite(String playlistToOverwrite) {
        this.playlistToOverwrite = playlistToOverwrite;
    }
}
