package com.omwan.latestadditions.utils;


import com.omwan.latestadditions.component.UriComponent;
import com.omwan.latestadditions.dto.PlaylistUri;
import mockit.Tested;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for UriComponent.
 */
public class UriComponentTest {

    @Tested
    private UriComponent uriComponent;

    @Before
    public void setup() {
        this.uriComponent = new UriComponent();
    }

    /**
     * Assert that a PlaylistUri instance can be built from a URI string
     * with the correct fields.
     */
    @Test
    public void testBuildPlaylistURI() {
        String userId = "userId";
        String playlistId = "playlistId";
        String uri = String.format("spotify:user:%s:playlist:%s", userId, playlistId);
        PlaylistUri uriWrapper = uriComponent.buildPlaylistURI(uri);
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
        uriComponent.buildPlaylistURI(uri);
    }
}