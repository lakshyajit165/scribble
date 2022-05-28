package com.scribble.authservice.dto;

import com.scribble.authservice.model.AuthErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GenericAuthResponse {

    private AuthErrorCode authErrorCode;
    private String message;

}
