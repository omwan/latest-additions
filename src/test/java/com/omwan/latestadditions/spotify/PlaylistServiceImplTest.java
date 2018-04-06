package com.omwan.latestadditions.spotify;

import com.omwan.latestadditions.component.SpotifyApiComponent;
import com.omwan.latestadditions.component.UriUtils;
import com.omwan.latestadditions.component.UserPlaylistComponent;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import com.wrapper.spotify.requests.data.AbstractDataRequest;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for playlist services.
 */
public class PlaylistServiceImplTest {

    @Tested
    private PlaylistService playlistService;

    @Injectable
    private SpotifyApiComponent spotifyApiComponent;

    @Injectable
    private UserPlaylistComponent userPlaylistComponent;

    @Before
    public void setup() {
        playlistService = new PlaylistServiceImpl();
    }

    /**
     * Assert that the appropriate spotify API methods are called with the given
     * parameters to retrieve the user's playlists.
     */
    @Test
    public void testGetUserPlaylists() throws Exception {
        final String playlistName = "playlist";
        final int offset = 5;
        final int limit = 10;

        new Expectations() {{
            spotifyApiComponent.tokensExist();
            returns(true);

            spotifyApiComponent.getApiWithTokens();
            returns(buildMockedSpotifyApi());

            spotifyApiComponent.executeRequest((AbstractDataRequest) any, anyString);
            returns(buildMockedUserPlaylists(playlistName, offset, limit));
        }};

        Paging<PlaylistSimplified> actual = playlistService.getUserPlaylists(offset, limit);
        assertEquals(offset, (int) actual.getOffset());
    }

    /**
     * Assert that if the session attributes containing the access tokens are
     * not set, the function returns an empty response.
     */
    @Test
    public void testGetUserPlaylistsMissingTokens() throws Exception {
        new Expectations() {{
            spotifyApiComponent.tokensExist();
            returns(false);
        }};

        assertNull(playlistService.getUserPlaylists(5, 10));
    }

    /**
     * Assert that the appropriate spotify API methods are called with the given
     * parameters to retrieve an individual playlist's details.
     */
    @Test
    public void testGetPlaylistDetails() throws Exception {
        final String playlistUri = "spotify:user:123:playlist:456";

        new Expectations() {{
            spotifyApiComponent.getApiWithTokens();
            returns(buildMockedSpotifyApi());

            spotifyApiComponent.executeRequest((AbstractDataRequest) any, anyString);
            returns(buildMockedPlaylist(playlistUri));
        }};

        Playlist actual = playlistService.getPlaylistDetails(playlistUri);
        assertEquals(playlistUri, actual.getUri());
    }

    /**
     * Assert that the appropriate user playlist component and spotify API methods
     * are called with the given parameters to retrieve an individual playlist's details.
     */
    @Test
    public void testGetExistingPlaylists() throws Exception {
        final String userId = "user ID";
        final String playlistUri = "spotify:user:123:playlist:456";

        new Expectations() {{
            spotifyApiComponent.tokensExist();
            returns(true);

            spotifyApiComponent.getCurrentUserId();
            returns(userId);

            userPlaylistComponent.getPlaylistsForUser(userId);
            returns(Collections.singletonList(UriUtils.buildPlaylistUri(playlistUri)));

            spotifyApiComponent.getApiWithTokens();
            returns(buildMockedSpotifyApi());

            spotifyApiComponent.executeRequest((AbstractDataRequest) any, anyString);
            returns(buildMockedPlaylist(playlistUri));
        }};

        List<Playlist> actual = playlistService.getExistingPlaylists();
        assertTrue(actual.size() > 0);
        Playlist actualPlaylist = actual.get(0);
        assertEquals(playlistUri, actualPlaylist.getUri());
    }

    /**
     * Assert that if the session attributes containing the access tokens are
     * not set, the function returns an empty response.
     */
    @Test
    public void testGetExistingPlaylistsMissingTokens() throws Exception {
        new Expectations() {{
            spotifyApiComponent.tokensExist();
            returns(false);
        }};

        assertNull(playlistService.getExistingPlaylists());
    }

    /**
     * Assert that the appropriate user playlist component method is called
     * with the given playlist URI string.
     */
    @Test
    public void testDeleteSavedPlaylist() throws Exception {
        final String playlistUri = "spotify:user:123:playlist:456";

        playlistService.deleteSavedPlaylist(playlistUri);

        new Verifications() {{
            userPlaylistComponent.deleteSavedPlaylist(playlistUri);
        }};
    }

    /**
     * Create a mocked instance of a playlist paging object with the given parameters.
     *
     * @param playlistName name of playlist to add to paging object
     * @param offset       offset for paging object
     * @param limit        limit for paging object
     * @return mocked paging object
     */
    private Paging<PlaylistSimplified> buildMockedUserPlaylists(String playlistName,
                                                                int offset,
                                                                int limit) {
        PlaylistSimplified playlist = new PlaylistSimplified.Builder()
                .setName(playlistName)
                .build();

        return new Paging.Builder<PlaylistSimplified>()
                .setItems(new PlaylistSimplified[]{playlist})
                .setLimit(limit)
                .setOffset(offset)
                .build();
    }

    /**
     * Create a mocked instance of a playlist with the given URI.
     *
     * @param playlistUri uri for playlist
     * @return mocked playlist
     */
    private Playlist buildMockedPlaylist(String playlistUri) {
        return new Playlist.Builder()
                .setUri(playlistUri)
                .build();
    }

    /**
     * Create a mocked instance of a spotify API with the necessary fields
     * set to make an API call.
     *
     * @return mocked spotify API object
     */
    private SpotifyApi buildMockedSpotifyApi() {
        return new SpotifyApi.Builder()
                .setClientId("client")
                .setClientSecret("secret")
                .setRedirectUri(URI.create("redirect"))
                .setAccessToken("access token")
                .setRefreshToken("refresh token")
                .build();
    }

}