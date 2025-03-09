package com.example.bid_api.model.request;

import lombok.Data;

@Data
public class UserRequest {
    private String userId;
    private String username;
    private String password;
    private String role;
}
