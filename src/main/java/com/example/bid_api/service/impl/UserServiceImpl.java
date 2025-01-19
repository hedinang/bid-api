package com.example.bid_api.service.impl;

import com.example.bid_api.model.dto.CustomUserDetails;
import com.example.bid_api.model.dto.Me;
import com.example.bid_api.model.entity.User;
import com.example.bid_api.model.request.LoginRequest;
import com.example.bid_api.repository.mongo.UserRepository;
import com.example.bid_api.service.UserService;
import com.example.bid_api.util.constant.ErrorCode;
import com.example.bid_api.util.exception.ServiceException;
import com.example.bid_api.util.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;

    public Map<String, Object> loginUser(LoginRequest request) {
        try {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
            Authentication authentication = authenticationManager.authenticate(authToken);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            User user = userDetails.getUser();
            user.setUserId(userDetails.getUser().getUserId());
            String jwt = tokenProvider.generateToken(user);
            user = userRepository.findByUserId(user.getUserId()).orElseThrow(() -> new ServiceException("user not found"));
            String accessToken = user.getAccessToken();

            if (!tokenProvider.validateToken(user.getAccessToken())) {
                user.setAccessToken(jwt);
                userRepository.save(user);
                accessToken = jwt;
            }

            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getUserId());
            response.put("token", accessToken);
            return response;
        } catch (Exception e) {
            log.error("login with email {} error: {}", request.getUsername(), e.getMessage());
            return Collections.emptyMap();
        }
    }

    @Override
    public User findByAccessToken(String token) {
        return userRepository.findByAccessToken(token);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ServiceException(ErrorCode.E404.code(), "User not found"));
        return new CustomUserDetails(user);
    }

    @Override
    public Me getMe(User user) {
        User currentUser = userRepository.findByUserId(user.getUserId()).orElse(null);
        Me me = new Me();
        me.setUser(currentUser);
        return me;
    }

    @Override
    public boolean logout(String userId) {
        Optional<User> userOptional = userRepository.findByUserId(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setAccessToken(null);
            userRepository.save(user);
            return true;
        }
        return false;
    }

}
