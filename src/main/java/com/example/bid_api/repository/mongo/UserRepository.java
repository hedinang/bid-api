package com.example.bid_api.repository.mongo;

import com.example.bid_api.model.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    User findByAccessToken(String accessToken);

    Optional<User> findByUsername(String username);

    Optional<User> findByUserId(String userId);
}
