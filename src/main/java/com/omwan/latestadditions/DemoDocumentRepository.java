package com.omwan.latestadditions;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DemoDocumentRepository extends MongoRepository<DemoDocument, String> {
}
