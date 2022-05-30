package com.scribble.authservice.dto;

import com.scribble.authservice.model.HttpStatusCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GenericAuthResponse {

    private HttpStatusCode httpStatusCode;
    private String message;

}
