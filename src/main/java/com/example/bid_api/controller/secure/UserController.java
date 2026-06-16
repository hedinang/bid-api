package com.example.bid_api.controller.secure;

import com.example.bid_api.model.entity.User;
import com.example.bid_api.model.request.ChangePasswordRequest;
import com.example.bid_api.model.request.PageRequest;
import com.example.bid_api.model.request.UploadFileRequest;
import com.example.bid_api.model.request.UserRequest;
import com.example.bid_api.model.search.UserSearch;
import com.example.bid_api.service.ResourceService;
import com.example.bid_api.service.UserService;
import com.example.bid_api.util.exception.ServiceException;
import com.example.bid_api.util.response.BaseResponse;
import com.example.bid_api.util.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/secure/user")
public class UserController {
    private final UserService userService;
    private final ResourceService resourceService;

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

    @PostMapping("/change-password")
    public BaseResponse<Object> changePasswordUser(@RequestBody @Valid ChangePasswordRequest request, @AuthenticationPrincipal User user) {
        try {
            return Response.toData(userService.changePassword(user.getUserId(), request));
        } catch (ServiceException e) {
            return Response.toError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        } catch (Exception e) {
            return Response.toError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred");
        }
    }

    @PostMapping("/store-user")
    public BaseResponse<Object> getUserList(@RequestBody UserRequest request, @AuthenticationPrincipal User user) {
        return Response.toData(userService.store(request));
    }

    @PostMapping("/update")
    public BaseResponse<Object> update(@RequestBody UserRequest request, @AuthenticationPrincipal User user) {
        return Response.toData(userService.update(request));
    }

    @PostMapping("/reset-password/{userId}")
    public BaseResponse<String> resetPassword(@PathVariable String userId, @AuthenticationPrincipal User user) {
//        if (!Objects.equals(user.getRoleCode(), "ADMIN")) return new BaseResponse<>(403, "Dont have permission", null);

        userService.resetPassword(userId, user);
        return new BaseResponse<>(200, "Delete user successfully", null);
    }

    @PostMapping("/upload-profile-image")
    public BaseResponse<Object> upload(@RequestBody UploadFileRequest req, @AuthenticationPrincipal User user) {
        boolean response = resourceService.uploadProfileImage(req, user);

        if (response) {
            return Response.toData(response);
        } else {
            return new BaseResponse<>(403, "pls update an image");
        }
    }
}
