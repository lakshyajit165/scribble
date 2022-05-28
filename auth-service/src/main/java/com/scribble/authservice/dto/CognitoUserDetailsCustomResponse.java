package com.scribble.authservice.dto;

import com.scribble.authservice.model.CognitoUserAccountStatus;
import com.scribble.authservice.model.CognitoUserStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CognitoUserDetailsCustomResponse {
    private CognitoUserStatus cognitoUserStatus;
    private CognitoUserAccountStatus cognitoUserAccountStatus;
    private String message;
}
