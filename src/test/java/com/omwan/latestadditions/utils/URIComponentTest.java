package com.omwan.latestadditions.utils;


import com.omwan.latestadditions.component.UriComponent;
import com.omwan.latestadditions.dto.PlaylistURIWrapper;
import mockit.Tested;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

//import static org.junit.Assert.assertEquals;

public class URIComponentTest {

    @Tested
    private UriComponent uriComponent;

    @Before
    public void setup() {
        this.uriComponent = new UriComponent();
    }

    @Test
    public void testBuildPlaylistURI() {
        String userId = "userId";
        String playlistId = "playlistId";
        String uri = String.format("spotify:user:%s:playlist:%s", userId, playlistId);
        PlaylistURIWrapper uriWrapper = uriComponent.buildPlaylistURI(uri);
        assertEquals(userId, uriWrapper.getUserId());
        assertEquals(playlistId, uriWrapper.getPlaylistId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildPlaylistURIMalformedURI() {
        String uri = "";
        uriComponent.buildPlaylistURI(uri);
    }
}