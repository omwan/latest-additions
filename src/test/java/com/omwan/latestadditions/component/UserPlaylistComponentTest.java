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
        final String playlistUri = String.format("spotify:user:%s:playlist:456", userId);

        final UserPlaylist expected = createMockedUserPlaylist(userId, playlistUri);

        new Expectations() {{
            userPlaylistRepository.findByUserId(userId);
            returns(Collections.singletonList(expected));
        }};

        List<PlaylistIdWrapper> actual = userPlaylistComponent.getPlaylistsForUser(userId);
        assertTrue(actual.size() > 0);
        PlaylistIdWrapper playlist = actual.get(0);
        assertEquals(playlist.toString(), playlistUri);
    }

    /**
     * Assert that the appropriate repository method is called with the
     * given playlist URI string.
     */
    @Test
    public void testDeleteSavedPlaylist() {
        final String playlistUri = "spotify:user:123:playlist:456";

        new Expectations() {{
            userPlaylistRepository.deleteByPlaylistId(playlistUri);
            returns(1);
        }};

        userPlaylistComponent.deleteSavedPlaylist(playlistUri);
    }

    /**
     * Assert that if no playlists matching the given playlistUri are deleted,
     * the appropriate exception is thrown.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteSavedPlaylistInvalidUri() {
        final String playlistUri = "spotify:user:123:playlist:456";

        new Expectations() {{
            userPlaylistRepository.deleteByPlaylistId(playlistUri);
            returns(0);
        }};

        userPlaylistComponent.deleteSavedPlaylist(playlistUri);
    }

    /**
     * Create a mocked UserPlaylist document instance with the given parameters.
     *
     * @param userId      user ID to mock
     * @param playlistUri playlist URI to mock
     * @return mocked UserPlaylist object
     */
    private UserPlaylist createMockedUserPlaylist(String userId, String playlistUri) {
        UserPlaylist userPlaylist = new UserPlaylist();
        userPlaylist.setUserId(userId);
        userPlaylist.setPlaylistId(playlistUri);
        return userPlaylist;
    }
}