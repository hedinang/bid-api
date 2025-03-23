package com.example.bid_api.repository.mongo;

import com.example.bid_api.model.entity.User;
import com.example.bid_api.repository.mongo.custom.CustomUserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String>, CustomUserRepository {
    User findByAccessToken(String accessToken);

    User findByUsername(String username);

    Optional<User> findByUserId(String userId);

    List<User> findByUserIdIn(List<String> userIds);
}
