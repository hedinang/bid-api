package com.example.bid_api.controller;

import com.example.bid_api.model.entity.User;
import com.example.bid_api.service.UserService;
import com.example.bid_api.util.response.BaseResponse;
import com.example.bid_api.util.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    @GetMapping("/getMe")
    public BaseResponse<Object> getMe(@AuthenticationPrincipal User user) {
        return Response.toData(userService.getMe(user));
    }

    @GetMapping("/logout")
    public BaseResponse<Object> logoutUser(@AuthenticationPrincipal User user) {
        if (userService.logout(user.getUserId())) {
            return Response.toData(user.getUserId());
        }
        return Response.toError(HttpStatus.BAD_REQUEST.value(), "logout fail");
    }
}
