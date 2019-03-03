package com.omwan.latestadditions.service;


import com.omwan.latestadditions.SpotifyTestUtils;
import com.omwan.latestadditions.component.SpotifyApiComponent;
import com.omwan.latestadditions.component.UserPlaylistComponent;
import com.omwan.latestadditions.dto.PlaylistIdWrapper;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.requests.data.AbstractDataRequest;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import org.apache.commons.lang3.builder.ToStringBuilder;
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
        final String playlistId = "playlist ID";

        new Expectations() {{
            spotifyApiComponent.tokensExist();
            returns(true);

            spotifyApiComponent.getCurrentUserId();
            returns(userId);

            userPlaylistComponent.getPlaylistsForUser(userId);
            returns(Collections.singletonList(new PlaylistIdWrapper(playlistId, userId)));

            spotifyApiComponent.getApiWithTokens();
            returns(SpotifyTestUtils.buildMockedSpotifyApi());

            spotifyApiComponent.executeRequest((AbstractDataRequest) any, anyString);
            returns(SpotifyTestUtils.buildMockedPlaylist(playlistId));
        }};

        List<Playlist> actual = savedPlaylistService.getExistingPlaylists();
        assertTrue(actual.size() > 0);
        Playlist actualPlaylist = actual.get(0);
        System.out.println(ToStringBuilder.reflectionToString(actualPlaylist));
        assertEquals(playlistId, actualPlaylist.getId());
    }

    /**
     * Assert that if the cookies containing the access tokens are
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
     * with the given playlist ID string.
     */
    @Test
    public void testDeleteSavedPlaylist() throws Exception {
        final String playlistId = "playlist ID";

        savedPlaylistService.deleteSavedPlaylist(playlistId);

        new Verifications() {{
            userPlaylistComponent.deleteSavedPlaylist(playlistId);
        }};
    }

}