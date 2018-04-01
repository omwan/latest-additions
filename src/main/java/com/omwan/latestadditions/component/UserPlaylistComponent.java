package com.omwan.latestadditions.component;

import com.omwan.latestadditions.dto.PlaylistUri;
import com.omwan.latestadditions.mongo.UserPlaylist;
import com.omwan.latestadditions.mongo.UserPlaylistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserPlaylistComponent {

    @Autowired
    private UserPlaylistRepository userPlaylistRepository;

    @Autowired
    private UriComponent uriComponent;

    public PlaylistUri getPlaylistUriForUser(String userId) {
        UserPlaylist userPlaylist = userPlaylistRepository.findByUserId(userId);
        return uriComponent.buildPlaylistURI(userPlaylist.getPlaylistUri());
    }

    public void saveUserPlaylist(String userId, String playlistUri) {
        UserPlaylist userPlaylist = new UserPlaylist();
        userPlaylist.setUserId(userId);
        userPlaylist.setPlaylistUri(playlistUri);
        userPlaylistRepository.save(userPlaylist);
    }
}
