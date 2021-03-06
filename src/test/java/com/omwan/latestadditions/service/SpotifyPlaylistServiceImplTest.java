package com.omwan.latestadditions.service;

import com.omwan.latestadditions.SpotifyTestUtils;
import com.omwan.latestadditions.component.SpotifyApiComponent;
import com.omwan.latestadditions.component.UserPlaylistComponent;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import com.wrapper.spotify.requests.data.AbstractDataRequest;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Unit tests for spotify playlist services.
 */
public class SpotifyPlaylistServiceImplTest {

    @Tested
    private SpotifyPlaylistService spotifyPlaylistService;

    @Injectable
    private SpotifyApiComponent spotifyApiComponent;

    @Injectable
    private UserPlaylistComponent userPlaylistComponent;

    @Before
    public void setup() {
        spotifyPlaylistService = new SpotifyPlaylistServiceImpl();
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
            returns(SpotifyTestUtils.buildMockedSpotifyApi());

            spotifyApiComponent.executeRequest((AbstractDataRequest) any, anyString);
            returns(buildMockedUserPlaylists(playlistName, offset, limit));
        }};

        Paging<PlaylistSimplified> actual = spotifyPlaylistService.getUserPlaylists(offset, limit);
        assertEquals(offset, (int) actual.getOffset());
    }

    /**
     * Assert that if the cookies containing the access tokens are
     * not set, the function returns an empty response.
     */
    @Test
    public void testGetUserPlaylistsMissingTokens() throws Exception {
        new Expectations() {{
            spotifyApiComponent.tokensExist();
            returns(false);
        }};

        assertNull(spotifyPlaylistService.getUserPlaylists(5, 10));
    }

    /**
     * Assert that the appropriate spotify API methods are called with the given
     * parameters to retrieve an individual playlist's details.
     */
    @Test
    public void testGetPlaylistDetails() throws Exception {
        final String playlistId = "playlist ID";

        new Expectations() {{
            spotifyApiComponent.getApiWithTokens();
            returns(SpotifyTestUtils.buildMockedSpotifyApi());

            spotifyApiComponent.getCurrentUserId();
            returns("user ID");

            spotifyApiComponent.executeRequest((AbstractDataRequest) any, anyString);
            returns(SpotifyTestUtils.buildMockedPlaylist(playlistId));
        }};

        Playlist actual = spotifyPlaylistService.getPlaylistDetails(playlistId);
        assertEquals(playlistId, actual.getId());
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
}