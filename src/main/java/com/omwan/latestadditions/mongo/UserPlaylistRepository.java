package com.omwan.latestadditions.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository to manage data in "userPlaylist" Mongo collection.
 */
@Repository
public interface UserPlaylistRepository extends MongoRepository<UserPlaylist, String> {

    /**
     * Retrieve all documents for the given userId.
     *
     * @param userId user ID to retrieve documents for
     * @return list of documents
     */
    List<UserPlaylist> findByUserId(String userId);

    /**
     * Delete a docuemnt with the given playlist uri.
     *
     * @param playlistUri uri of playlist to delete
     * @return number of deleted documents
     */
    int deleteByPlaylistUri(String playlistUri);
}
