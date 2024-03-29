package com.scribble.authservice.dto;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class UserSignUpOrForgotPasswordRequest {
    @NotBlank(message = "Email address is required")
    @Email(message = "Email address is not valid")
    private String email;
}
