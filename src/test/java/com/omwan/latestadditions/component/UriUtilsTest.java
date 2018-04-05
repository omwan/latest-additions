package com.omwan.latestadditions.component;


import com.omwan.latestadditions.dto.PlaylistUri;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for UriUtils.
 */
public class UriUtilsTest {

    /**
     * Assert that a PlaylistUri instance can be built from a URI string
     * with the correct fields.
     */
    @Test
    public void testBuildPlaylistURI() {
        String userId = "userId";
        String playlistId = "playlistId";
        String uri = String.format("spotify:user:%s:playlist:%s", userId, playlistId);
        PlaylistUri uriWrapper = UriUtils.buildPlaylistUri(uri);
        assertEquals(userId, uriWrapper.getUserId());
        assertEquals(playlistId, uriWrapper.getPlaylistId());
    }

    /**
     * Assert that trying to build a PlaylistUri instance with a malformed
     * string throws the appropriate exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testBuildPlaylistURIMalformedURI() {
        String uri = "";
        UriUtils.buildPlaylistUri(uri);
    }
}