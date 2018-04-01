package com.omwan.latestadditions.spotify;

import com.omwan.latestadditions.component.SpotifyApiComponent;
import com.omwan.latestadditions.component.UriComponent;
import com.omwan.latestadditions.dto.BuildPlaylistRequest;
import com.omwan.latestadditions.dto.PlaylistUri;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.exceptions.detailed.UnauthorizedException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import com.wrapper.spotify.model_objects.specification.User;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import com.wrapper.spotify.requests.data.AbstractDataRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Services pertaining to spotify API.
 */
@Service
public class SpotifyServiceImpl implements SpotifyService {

    @Autowired
    private SpotifyApiComponent spotifyApiComponent;

    @Autowired
    private UriComponent uriComponent;

    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private HttpSession session;

    /**
     * Make authorization request for API usage.
     *
     * @param response to redirect to auth request page.
     */
    @Override
    public void authorize(HttpServletResponse response) {
        List<String> scopes = Arrays.asList(
                "playlist-read-private",
                "playlist-modify-private",
                "playlist-modify-public",
                "playlist-read-collaborative");

        SpotifyApi spotifyApi = spotifyApiComponent.getSpotifyApi();
        AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
                .scope(String.join(", ", scopes))
                .show_dialog(true)
                .build();
        URI redirectURI = authorizationCodeUriRequest.execute();

        try {
            response.sendRedirect(redirectURI.toString());
        } catch (IOException e) {
            throw new RuntimeException("Could not redirect to permissions page", e);
        }
    }

    /**
     * Set the access token for the spotifyApi instance as given by the redirect.
     *
     * @param token    access token
     * @param response to redirect to application homepage after receiving auth token.
     */
    @Override
    public void setToken(String token, HttpServletResponse response) {
        SpotifyApi spotifyApi = spotifyApiComponent.getSpotifyApi();
        AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(token)
                .build();
        try {
            AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();
            session.setAttribute("ACCESS_TOKEN", authorizationCodeCredentials.getAccessToken());
            session.setAttribute("REFRESH_TOKEN", authorizationCodeCredentials.getRefreshToken());
            response.sendRedirect("/");
        } catch (IOException | SpotifyWebApiException e) {
            throw new RuntimeException("Could not retrieve auth code credentials", e);
        }
    }

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

        SpotifyApi spotifyApi = getApiWithTokens();

        AbstractDataRequest usersPlaylistsRequest = spotifyApi
                .getListOfCurrentUsersPlaylists()
                .limit(limit)
                .offset(offset)
                .build();
        return executeRequest(usersPlaylistsRequest, "Unable to retrieve user playlists");
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

        SpotifyApi spotifyApi = getApiWithTokens();

        AbstractDataRequest playlistDetailsRequest = spotifyApi
                .getPlaylist(playlistURIWrapper.getUserId(), playlistURIWrapper.getPlaylistId())
                .fields(fields)
                .build();
        return executeRequest(playlistDetailsRequest, "Unable to retrieve playlist details");
    }

    @Override
    public PlaylistUri buildLatestAdditionsPlaylist(BuildPlaylistRequest request) {
        List<PlaylistUri> playlists = request.getPlaylistUris().keySet().stream()
                .map(uriComponent::buildPlaylistURI)
                .collect(Collectors.toList());

        Map<PlaylistUri, LinkedList<PlaylistTrack>> playlistTracks = getPlaylistTracks(request, playlists);

        List<PlaylistTrack> latestAdditionsTracks = getLatestAdditions(request, playlistTracks);

        String userId = getUserId();
        String[] trackUris = latestAdditionsTracks.stream()
                .map(playlistTrack -> playlistTrack.getTrack().getUri())
                .collect(Collectors.toList())
                .toArray(new String[latestAdditionsTracks.size()]);

        if (request.isOverwriteExisting()) {
            return overwriteExistingLatestAdditions(trackUris, userId);
        } else {
            return createNewLatestAdditions(trackUris, userId, request);
        }
    }

    private Map<PlaylistUri, LinkedList<PlaylistTrack>> getPlaylistTracks(BuildPlaylistRequest request,
                                                                          List<PlaylistUri> playlists) {
        Map<PlaylistUri, LinkedList<PlaylistTrack>> playlistTracks = new HashMap<>();

        for (PlaylistUri playlist : playlists) {
            int limit = request.getNumTracks();
            int playlistSize = request.getPlaylistUris().get(playlist.toString());
            int offset = playlistSize - request.getNumTracks();
            PlaylistTrack[] tracks = getTracksForPlaylist(playlist, limit, offset);
            playlistTracks.put(playlist, new LinkedList<>(Arrays.asList(tracks)));
        }

        return playlistTracks;
    }

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

    private PlaylistUri overwriteExistingLatestAdditions(String[] trackUris,
                                                         String userId) {
        SpotifyApi spotifyApi = getApiWithTokens();
        String uri = userId.concat(userId);
        PlaylistUri playlistUri = uriComponent.buildPlaylistURI(uri);
        AbstractDataRequest replaceTracksRequest = spotifyApi
                .replacePlaylistsTracks(userId, playlistUri.getPlaylistId(), trackUris)
                .build();
        executeRequest(replaceTracksRequest, "ugh");
        return playlistUri;
    }

    private PlaylistUri createNewLatestAdditions(String[] trackUris,
                                                 String userId,
                                                 BuildPlaylistRequest request) {
        SpotifyApi spotifyApi = getApiWithTokens();
        AbstractDataRequest createPlaylistRequest = spotifyApi
                .createPlaylist(userId, request.getPlaylistName())
                .description("Autogenerated playlist")
                .collaborative(request.isCollaborative())
                .public_(request.isPublic())
                .build();
        Playlist latestAdditions = executeRequest(createPlaylistRequest, "ugh");
        PlaylistUri playlistUri = uriComponent.buildPlaylistURI(latestAdditions.getUri());
        AbstractDataRequest addTracksRequest = spotifyApi
                .addTracksToPlaylist(userId, playlistUri.getPlaylistId(), trackUris)
                .build();
        executeRequest(addTracksRequest, "ugh");
        return playlistUri;
    }

    private String getUserId() {
        if (session.getAttribute("USER_ID") == null) {
            SpotifyApi spotifyApi = getApiWithTokens();
            AbstractDataRequest userRequest = spotifyApi.getCurrentUsersProfile()
                    .build();
            User user = executeRequest(userRequest, "ugh");
            session.setAttribute("USER_ID", user.getId());
            return user.getId();
        } else {
            return session.getAttribute("USER_ID").toString();
        }
    }

    private PlaylistTrack[] getTracksForPlaylist(PlaylistUri uriWrapper, int limit, int offset) {
        SpotifyApi spotifyApi = getApiWithTokens();

        if (spotifyApi == null) {
            throw new RuntimeException("ugh");
        }
        AbstractDataRequest trackRequest = spotifyApi
                .getPlaylistsTracks(uriWrapper.getUserId(), uriWrapper.getPlaylistId())
                .limit(limit)
                .offset(offset)
                .build();
        Paging<PlaylistTrack> tracks = executeRequest(trackRequest, "ugh");
        return tracks.getItems();
    }

    private SpotifyApi getApiWithTokens() {
        SpotifyApi spotifyApi = spotifyApiComponent.getSpotifyApi();

        String accessToken = session.getAttribute("ACCESS_TOKEN").toString();
        String refreshToken = session.getAttribute("REFRESH_TOKEN").toString();

        spotifyApi.setAccessToken(accessToken);
        spotifyApi.setRefreshToken(refreshToken);

        return spotifyApi;
    }

    /**
     * Helper to execute API requests.
     *
     * @param requestBuilder request to execute
     * @param errorMessage   error message to log in event of failure
     * @param <T>            return type of request
     * @return value of executed request
     */
    private <T> T executeRequest(AbstractDataRequest requestBuilder,
                                 String errorMessage) {
        try {
            return requestBuilder.execute();
        } catch (UnauthorizedException e) {
            refreshToken();
            try {
                return requestBuilder.execute();
            } catch (IOException | SpotifyWebApiException ex) {
                throw new RuntimeException(errorMessage, e);
            }
        } catch (IOException | SpotifyWebApiException e) {
            throw new RuntimeException(errorMessage, e);
        }
    }

    /**
     * Refresh access token for spotify api.
     */
    private void refreshToken() {
        SpotifyApi spotifyApi = spotifyApiComponent.getSpotifyApi();
        spotifyApi.setRefreshToken(session.getAttribute("REFRESH_TOKEN").toString());

        try {
            AuthorizationCodeCredentials authorizationCodeCredentials = spotifyApi.authorizationCodeRefresh()
                    .build()
                    .execute();

            session.setAttribute("ACCESS_TOKEN", authorizationCodeCredentials.getAccessToken());
            session.setAttribute("REFRESH_TOKEN", authorizationCodeCredentials.getAccessToken());

            System.out.println(String.format("Token successfully refreshed, expires in %s",
                    authorizationCodeCredentials.getExpiresIn()));
        } catch (IOException | SpotifyWebApiException e) {
            throw new RuntimeException("Unable to refresh access token", e);
        }
    }

}
