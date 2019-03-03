package com.omwan.latestadditions.component;

import com.omwan.latestadditions.dto.PlaylistIdWrapper;
import com.omwan.latestadditions.mongo.UserPlaylist;
import com.omwan.latestadditions.mongo.UserPlaylistRepository;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for UserPlaylistComponent.
 */
public class UserPlaylistComponentTest {

    @Tested
    private UserPlaylistComponent userPlaylistComponent;

    @Injectable
    private UserPlaylistRepository userPlaylistRepository;

    @Before
    public void setup() {
        userPlaylistComponent = new UserPlaylistComponent();
    }

    /**
     * Assert that the user playlist objects returned by the repository
     * are appropriately converted into PlaylistIdWrapper objects.
     */
    @Test
    public void testGetPlaylistsForUser() {
        final String userId = "123";
        final String playlistId = "playlistId";

        final UserPlaylist expected = new UserPlaylist(userId, playlistId);

        new Expectations() {{
            userPlaylistRepository.findByUserId(userId);
            returns(Collections.singletonList(expected));
        }};

        List<PlaylistIdWrapper> actual = userPlaylistComponent.getPlaylistsForUser(userId);
        assertTrue(actual.size() > 0);
        PlaylistIdWrapper playlist = actual.get(0);
        assertEquals(playlist.getPlaylistId(), playlistId);
    }

    /**
     * Assert that the appropriate repository method is called with the
     * given playlist ID string.
     */
    @Test
    public void testDeleteSavedPlaylist() {
        final String playlistId = "playlistId";

        new Expectations() {{
            userPlaylistRepository.deleteByPlaylistId(playlistId);
            returns(1);
        }};

        userPlaylistComponent.deleteSavedPlaylist(playlistId);
    }

    /**
     * Assert that if no playlists matching the given playlist ID are deleted,
     * the appropriate exception is thrown.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteSavedPlaylistInvalidUri() {
        final String playlistId = "playlistId";

        new Expectations() {{
            userPlaylistRepository.deleteByPlaylistId(playlistId);
            returns(0);
        }};

        userPlaylistComponent.deleteSavedPlaylist(playlistId);
    }
}