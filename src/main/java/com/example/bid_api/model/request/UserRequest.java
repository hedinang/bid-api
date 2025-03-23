package com.example.bid_api.model.request;

import lombok.Data;

@Data
public class UserRequest {
    private String userId;
    private String username;
    private String password;
    private String name;
    private String email;
    private String phone;
    private String role;
    private String status;
}
