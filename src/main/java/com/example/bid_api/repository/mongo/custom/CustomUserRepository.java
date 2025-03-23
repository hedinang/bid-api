package com.example.bid_api.repository.mongo.custom;

import com.example.bid_api.model.entity.User;
import com.example.bid_api.model.request.PageRequest;
import com.example.bid_api.model.search.UserSearch;

import java.util.List;

public interface CustomUserRepository {
    List<User> getUserList(PageRequest<UserSearch> request);

    long countUserList(UserSearch request);
}
