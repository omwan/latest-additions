package com.omwan.latestadditions.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPlaylistRepository extends MongoRepository<UserPlaylist, String> {

    List<UserPlaylist> findByUserId(String userId);
}
