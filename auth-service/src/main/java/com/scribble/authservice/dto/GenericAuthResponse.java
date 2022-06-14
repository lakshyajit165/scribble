package com.scribble.authservice.dto;

import com.scribble.authservice.model.HttpStatusCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GenericAuthResponse {
    private String message;
}
