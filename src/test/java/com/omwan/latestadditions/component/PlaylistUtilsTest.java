package com.omwan.latestadditions.component;


import com.omwan.latestadditions.dto.PlaylistIdWrapper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for PlaylistUtils.
 */
public class PlaylistUtilsTest {

    /**
     * Assert that a PlaylistIdWrapper instance can be built from a URI string
     * with the correct fields.
     */
    @Test
    public void testBuildPlaylistURI() {
        String userId = "userId";
        String playlistId = "playlistId";
        String uri = String.format("spotify:user:%s:playlist:%s", userId, playlistId);
        PlaylistIdWrapper uriWrapper = PlaylistUtils.buildPlaylistWrapper(uri, userId);
        assertEquals(userId, uriWrapper.getUserId());
        assertEquals(playlistId, uriWrapper.getPlaylistId());
    }

    /**
//     * Assert that trying to build a PlaylistIdWrapper instance with a malformed
//     * string throws the appropriate exception.
//     */
//    @Test(expected = IllegalArgumentException.class)
//    public void testBuildPlaylistURIMalformedURI() {
//        String uri = "";
//        String userId = "";
//        PlaylistUtils.buildPlaylistWrapper(uri, userId);
//    }
}