package com.example.bid_api.controller;

import com.example.bid_api.model.entity.User;
import com.example.bid_api.model.request.PageRequest;
import com.example.bid_api.model.request.UserRequest;
import com.example.bid_api.model.search.UserSearch;
import com.example.bid_api.service.UserService;
import com.example.bid_api.util.response.BaseResponse;
import com.example.bid_api.util.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
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

    @PostMapping("/list")
    public BaseResponse<Object> getUserList(@RequestBody PageRequest<UserSearch> request, @AuthenticationPrincipal User user) {
        return Response.toData(userService.getUserList(request));
    }

    @PostMapping("/store-user")
    public BaseResponse<Object> getUserList(@RequestBody UserRequest request, @AuthenticationPrincipal User user) {
        return Response.toData(userService.store(request));
    }

//    @PostMapping("/update")
//    public BaseResponse<Object> updateUser(@RequestBody UserRequest request, @AuthenticationPrincipal User user) {
//        return Response.toData(userService.getUserList(request));
//    }
}
