package com.example.bid_api.service;

import com.example.bid_api.model.dto.Me;
import com.example.bid_api.model.entity.User;
import com.example.bid_api.model.request.LoginRequest;

import java.util.Map;

public interface UserService {
    Map<String, Object> loginUser(LoginRequest request);
    User findByAccessToken(String token);
    Me getMe(User user);
    boolean logout(String userId);
}
