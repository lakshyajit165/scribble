package com.scribble.authservice.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class UserConfirmForgotPasswordRequest {
    @NotBlank(message = "Email address is required")
    @Email(message = "Email address is not valid")
    private String email;

    /**
     * regex => Minimum eight characters, at least one letter, one number and one special character:
     * */
    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$", message = "Password is not valid")
    private String password;

    // AWS Cognito sends confirmation code of 6 digits by default
    @NotBlank(message = "Verification code is required")
    @Pattern(regexp = "^[0-9]{6}$", message = "Verification code is not valid")
    private String verification_code;
}
