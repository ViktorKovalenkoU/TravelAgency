package com.epam.finaltask.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignUpRequestDTO {

    @NotEmpty(message = "{username.required}")
    private String username;

    @NotEmpty(message = "{password.required}")
    @Size(min = 6, message = "{password.size}")
    private String password;

    @NotEmpty(message = "{password.confirm.required}")
    private String confirmPassword;

    @NotEmpty(message = "{email.required}")
    @Email(message = "{email.invalid}")
    private String email;

    @NotEmpty(message = "{phone.required}")
    @Pattern(
            regexp = "\\+?[0-9\\- ]{7,15}",
            message = "{phone.invalid}"
    )
    private String phoneNumber;

    @NotEmpty(message = "{role.required}")
    private String role;
}