package com.scribble.noteservice.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
@Getter
@Setter
public class ApiErrorDTO {

    private String message;
    private List<String> errors;

    //

    public ApiErrorDTO() {
        super();
    }

    public ApiErrorDTO(final String message, final List<String> errors) {
        super();
        this.message = message;
        this.errors = errors;
    }

    public ApiErrorDTO(final String message, final String error) {
        super();
        this.message = message;
        errors = Arrays.asList(error);
    }
}
