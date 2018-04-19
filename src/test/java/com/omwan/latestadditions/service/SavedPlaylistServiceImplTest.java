package com.omwan.latestadditions.service;


import com.omwan.latestadditions.SpotifyTestUtils;
import com.omwan.latestadditions.component.SpotifyApiComponent;
import com.omwan.latestadditions.component.UriUtils;
import com.omwan.latestadditions.component.UserPlaylistComponent;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.requests.data.AbstractDataRequest;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for saved playlist services.
 */
public class SavedPlaylistServiceImplTest {

    @Tested
    private SavedPlaylistService savedPlaylistService;

    @Injectable
    private SpotifyApiComponent spotifyApiComponent;

    @Injectable
    private UserPlaylistComponent userPlaylistComponent;

    @Before
    public void setup() {
        savedPlaylistService = new SavedPlaylistServiceImpl();
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
            returns(SpotifyTestUtils.buildMockedSpotifyApi());

            spotifyApiComponent.executeRequest((AbstractDataRequest) any, anyString);
            returns(SpotifyTestUtils.buildMockedPlaylist(playlistUri));
        }};

        List<Playlist> actual = savedPlaylistService.getExistingPlaylists();
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

        assertNull(savedPlaylistService.getExistingPlaylists());
    }

    /**
     * Assert that the appropriate user playlist component method is called
     * with the given playlist URI string.
     */
    @Test
    public void testDeleteSavedPlaylist() throws Exception {
        final String playlistUri = "spotify:user:123:playlist:456";

        savedPlaylistService.deleteSavedPlaylist(playlistUri);

        new Verifications() {{
            userPlaylistComponent.deleteSavedPlaylist(playlistUri);
        }};
    }

}