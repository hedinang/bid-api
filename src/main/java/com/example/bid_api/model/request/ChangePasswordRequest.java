package com.example.bid_api.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest {
    @NotBlank (message = "currentPassword cannot be empty")
    private String currentPassword;

    @NotEmpty(message = "newPassword cannot be empty")
    private String newPassword;

}
