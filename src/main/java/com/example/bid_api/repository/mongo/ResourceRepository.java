package com.example.bid_api.repository.mongo;

import com.example.bid_api.model.entity.Resource;
import com.example.bid_api.repository.mongo.custom.CustomResourceRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ResourceRepository extends MongoRepository<Resource, String>, CustomResourceRepository {
    void deleteByRequestUuidAndContentType(String requestUuid, String contentType);

    long countByRequestUuid(String requestUuid);

    List<Resource> findAllByRequestUuidOrderByChunkIndexAsc(String requestUuid);

    Optional<Resource> findFirstByResourceId(String resourceId);
}
