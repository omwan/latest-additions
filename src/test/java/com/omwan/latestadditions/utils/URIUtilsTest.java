package com.omwan.latestadditions.utils;


import com.omwan.latestadditions.dto.PlaylistURIWrapper;
import mockit.Tested;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

//import static org.junit.Assert.assertEquals;

public class URIUtilsTest {

    @Tested
    private URIUtils uriUtils;

    @Before
    public void setup() {
        this.uriUtils = new URIUtils();
    }

    @Test
    public void testBuildPlaylistURI() {
        String userId = "userId";
        String playlistId = "playlistId";
        String uri = String.format("spotify:user:%s:playlist:%s", userId, playlistId);
        PlaylistURIWrapper uriWrapper = uriUtils.buildPlaylistURI(uri);
        assertEquals(userId, uriWrapper.getUserId());
        assertEquals(playlistId, uriWrapper.getPlaylistId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildPlaylistURIMalformedURI() {
        String uri = "";
        uriUtils.buildPlaylistURI(uri);
    }
}