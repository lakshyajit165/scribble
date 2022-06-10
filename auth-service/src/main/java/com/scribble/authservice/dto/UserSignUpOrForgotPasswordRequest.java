package com.scribble.authservice.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class UserSignUpOrForgotPasswordRequest {
    @NotBlank(message = "Email address is required")
    @Email(message = "Email address is not valid")
    private String email;
}
