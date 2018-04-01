package com.omwan.latestadditions.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPlaylistRepository extends MongoRepository<UserPlaylist, String> {

    UserPlaylist findByUserId(String userId);
}
