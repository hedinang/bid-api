package com.example.bid_api.controller;

import com.example.bid_api.model.request.LoginRequest;
import com.example.bid_api.service.UserService;
import com.example.bid_api.util.response.BaseResponse;
import com.example.bid_api.util.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;

    @PostMapping("/login")
    public BaseResponse<Map<String, Object>> login(@RequestBody @Valid LoginRequest req) {
        Map<String, Object> response = userService.loginUser(req);
        if (response.isEmpty()) {
            return new BaseResponse<>(HttpStatus.BAD_REQUEST.value(), "ID người dùng hoặc mật khẩu không đúng. Vui lòng thử lại.", null);
        }
        return Response.toData(response);
    }
}
