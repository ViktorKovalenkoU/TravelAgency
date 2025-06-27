package com.epam.finaltask.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordChangeDTO {

    private String token;

    @NotBlank(message = "Password is required")
    private String newPassword;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;
}