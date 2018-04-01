package com.omwan.latestadditions.spotify;

import com.omwan.latestadditions.component.SpotifyApiComponent;
import com.omwan.latestadditions.component.UriComponent;
import com.omwan.latestadditions.component.UserPlaylistComponent;
import com.omwan.latestadditions.dto.BuildPlaylistRequest;
import com.omwan.latestadditions.dto.PlaylistUri;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import com.wrapper.spotify.requests.data.AbstractDataRequest;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of services pertaining to playlists.
 */
@Service
public class PlaylistServiceImpl implements PlaylistService {

    @Autowired
    private SpotifyApiComponent spotifyApiComponent;

    @Autowired
    private UriComponent uriComponent;

    @Autowired
    private UserPlaylistComponent userPlaylistComponent;

    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private HttpSession session;

    /**
     * Get playlists for the current user.
     *
     * @param limit  max number of playlists to retrieve
     * @param offset offset for playlist pagination
     * @return paging object containing given set of user's playlists.
     */
    @Override
    public Paging<PlaylistSimplified> getUserPlaylists(int limit, int offset) {
        if (session.getAttribute("ACCESS_TOKEN") == null) {
            return null;
        }

        SpotifyApi spotifyApi = spotifyApiComponent.getApiWithTokens();

        AbstractDataRequest usersPlaylistsRequest = spotifyApi
                .getListOfCurrentUsersPlaylists()
                .limit(limit)
                .offset(offset)
                .build();
        return spotifyApiComponent.executeRequest(usersPlaylistsRequest,
                "Unable to retrieve user playlists");
    }

    /**
     * Get details for a given playlist.
     *
     * @param playlistURI string containing user and playlist IDs
     * @return playlist details object for given user and playlist ID
     */
    @Override
    public Playlist getPlaylistDetails(String playlistURI) {
        String fields = String.join(",", Arrays.asList("description",
                "external_urls", "href", "images", "name", "owner",
                "tracks.total", "uri", "isCollaborative", "isPublicAccess"));
        PlaylistUri playlistURIWrapper = uriComponent.buildPlaylistURI(playlistURI);

        SpotifyApi spotifyApi = spotifyApiComponent.getApiWithTokens();

        AbstractDataRequest playlistDetailsRequest = spotifyApi
                .getPlaylist(playlistURIWrapper.getUserId(), playlistURIWrapper.getPlaylistId())
                .fields(fields)
                .build();
        String errorMessage = String.format("Unable to retrieve playlist details for playlist %s", playlistURI);
        return spotifyApiComponent.executeRequest(playlistDetailsRequest, errorMessage);
    }

    /**
     * Get all playlists that the current user has previously created
     * using this application.
     *
     * @return list of playlists
     */
    @Override
    public List<Playlist> getExistingPlaylists() {
        if (session.getAttribute("ACCESS_TOKEN") == null) {
            return null;
        }

        String userId = spotifyApiComponent.getCurrentUserId();
        List<PlaylistUri> playlistsUris = userPlaylistComponent.getPlaylistsForUser(userId);

        List<Playlist> existingPlaylists = new ArrayList<>();

        for (PlaylistUri uri : playlistsUris) {
            SpotifyApi spotifyApi = spotifyApiComponent.getApiWithTokens();
            AbstractDataRequest getPlaylistRequest = spotifyApi.getPlaylist(userId, uri.getPlaylistId())
                    .fields("name,tracks(total),uri,id")
                    .build();
            String errorMessage = String.format("Unable to retrieve existing playlists for user %s", userId);
            Playlist playlist = spotifyApiComponent.executeRequest(getPlaylistRequest, errorMessage);
            existingPlaylists.add(playlist);
        }

        return existingPlaylists;
    }

    /**
     * Create or update latest additions playlist with the specified requirements.
     *
     * @param request playlist specifications
     * @return URI of created or updated playlist
     */
    @Override
    public PlaylistUri buildLatestAdditionsPlaylist(BuildPlaylistRequest request) {
        List<PlaylistUri> playlists = request.getPlaylistUris().keySet().stream()
                .map(uriComponent::buildPlaylistURI)
                .collect(Collectors.toList());

        Map<PlaylistUri, LinkedList<PlaylistTrack>> playlistTracks = getPlaylistTracks(request, playlists);

        List<PlaylistTrack> latestAdditionsTracks = getLatestAdditions(request, playlistTracks);

        String userId = spotifyApiComponent.getCurrentUserId();
        String[] trackUris = latestAdditionsTracks.stream()
                .map(playlistTrack -> playlistTrack.getTrack().getUri())
                .collect(Collectors.toList())
                .toArray(new String[latestAdditionsTracks.size()]);

        if (request.isOverwriteExisting()) {
            return overwriteExistingLatestAdditions(request.getPlaylistToOverwrite(), trackUris, userId);
        } else {
            return createNewLatestAdditions(trackUris, userId, request);
        }
    }

    /**
     * Build a mapping of playlists to a LinkedList of their most recent tracks.
     *
     * @param request   playlist specifications
     * @param playlists playlists to retrieve tracks for
     * @return mapping of playlists to their tracks
     */
    private Map<PlaylistUri, LinkedList<PlaylistTrack>> getPlaylistTracks(BuildPlaylistRequest request,
                                                                          List<PlaylistUri> playlists) {
        Map<PlaylistUri, LinkedList<PlaylistTrack>> playlistTracks = new HashMap<>();

        for (PlaylistUri playlist : playlists) {
            int limit = request.getNumTracks();
            int playlistSize = request.getPlaylistUris().get(playlist.toString());
            int offset = playlistSize - request.getNumTracks();
            if (offset < 0) {
                offset = 0;
            }
            PlaylistTrack[] tracks = getTracksForPlaylist(playlist, limit, offset);
            playlistTracks.put(playlist, new LinkedList<>(Arrays.asList(tracks)));
        }

        return playlistTracks;
    }

    /**
     * Generate a list of "latest additions" tracks from the given set of playlists.
     *
     * @param request        playlist specifications
     * @param playlistTracks mapping of playlist URIs to their tracks
     * @return list of tracks for latest additions playlist
     */
    private List<PlaylistTrack> getLatestAdditions(BuildPlaylistRequest request,
                                                   Map<PlaylistUri, LinkedList<PlaylistTrack>> playlistTracks) {
        List<PlaylistTrack> latestAdditionsTracks = new ArrayList<>();

        PlaylistTrack lastAdded = null;
        PlaylistUri lastAddedPlaylist = null;

        Map<PlaylistUri, PlaylistTrack> lastAddedTracks = new HashMap<>();
        for (PlaylistUri playlist : playlistTracks.keySet()) {
            lastAddedTracks.put(playlist, playlistTracks.get(playlist).removeLast());
        }

        int count = 0;
        while (count < request.getNumTracks()) {
            for (PlaylistUri playlist : lastAddedTracks.keySet()) {
                if (lastAddedTracks.get(playlist) != null) {
                    if (lastAdded == null) {
                        lastAdded = lastAddedTracks.get(playlist);
                        lastAddedPlaylist = playlist;
                    } else {
                        PlaylistTrack currentTrack = lastAddedTracks.get(playlist);
                        if (currentTrack.getAddedAt().after(lastAdded.getAddedAt())) {
                            lastAdded = currentTrack;
                            lastAddedPlaylist = playlist;
                        }
                    }
                }
            }

            latestAdditionsTracks.add(lastAdded);
            if (!playlistTracks.get(lastAddedPlaylist).isEmpty()) {
                lastAddedTracks.put(lastAddedPlaylist, playlistTracks.get(lastAddedPlaylist).removeLast());
            } else {
                lastAddedTracks.put(lastAddedPlaylist, null);
            }
            lastAdded = null;
            count++;
        }

        return latestAdditionsTracks;
    }

    /**
     * Overwrite an existing playlist with the "latest additions" tracks.
     *
     * @param playlistToOverwrite playlist to overwrite
     * @param trackUris           list of track URIs for playlist
     * @param userId              user ID of current user
     * @return URI of overwritten playlist
     */
    private PlaylistUri overwriteExistingLatestAdditions(String playlistToOverwrite,
                                                         String[] trackUris,
                                                         String userId) {
        SpotifyApi spotifyApi = spotifyApiComponent.getApiWithTokens();
        PlaylistUri playlistUri = uriComponent.buildPlaylistURI(playlistToOverwrite);
        AbstractDataRequest replaceTracksRequest = spotifyApi
                .replacePlaylistsTracks(userId, playlistUri.getPlaylistId(), trackUris)
                .build();

        String errorMessage = String.format("Unable to replace tracks for playlist %s", playlistUri);
        spotifyApiComponent.executeRequest(replaceTracksRequest, errorMessage);

        return playlistUri;
    }

    /**
     * Create a new playlist containing the "latest additions" tracks.
     *
     * @param trackUris list of track URIs for playlist
     * @param userId    user ID of current user
     * @param request   playlist specifications
     * @return URI of newly created playlist
     */
    private PlaylistUri createNewLatestAdditions(String[] trackUris,
                                                 String userId,
                                                 BuildPlaylistRequest request) {
        SpotifyApi spotifyApi = spotifyApiComponent.getApiWithTokens();
        AbstractDataRequest createPlaylistRequest = spotifyApi
                .createPlaylist(userId, request.getPlaylistName())
                .description(request.getDescription())
                .collaborative(request.isCollaborative())
                .public_(request.isPublic())
                .build();

        String createPlaylistErrorMessage = String.format("Unable to create playlist with given parameters %s",
                ToStringBuilder.reflectionToString(request));
        Playlist latestAdditions = spotifyApiComponent.executeRequest(createPlaylistRequest,
                createPlaylistErrorMessage);

        PlaylistUri playlistUri = uriComponent.buildPlaylistURI(latestAdditions.getUri());
        AbstractDataRequest addTracksRequest = spotifyApi
                .addTracksToPlaylist(userId, playlistUri.getPlaylistId(), trackUris)
                .build();

        String addTracksErrorMessage = String.format("Unable to add tracks to playlist %s", playlistUri);
        spotifyApiComponent.executeRequest(addTracksRequest, addTracksErrorMessage);
        userPlaylistComponent.saveUserPlaylist(userId, playlistUri.toString());

        return playlistUri;
    }

    /**
     * Retrieve the tracks for an individual playlist.
     *
     * @param uriWrapper URI of playlist to retrieve tracks for
     * @param limit      maximum number of tracks to retrieve
     * @param offset     offset to start retrieving tracks from
     * @return array of playlist tracks
     */
    private PlaylistTrack[] getTracksForPlaylist(PlaylistUri uriWrapper, int limit, int offset) {
        SpotifyApi spotifyApi = spotifyApiComponent.getApiWithTokens();

        AbstractDataRequest trackRequest = spotifyApi
                .getPlaylistsTracks(uriWrapper.getUserId(), uriWrapper.getPlaylistId())
                .limit(limit)
                .offset(offset)
                .build();

        String errorMessage = String.format("Unable to retrieve tracks for playlist %s", uriWrapper.toString());
        Paging<PlaylistTrack> tracks = spotifyApiComponent.executeRequest(trackRequest, errorMessage);
        return tracks.getItems();
    }


}
