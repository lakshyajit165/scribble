package com.scribble.authservice.dto;


import com.scribble.authservice.model.HttpStatusCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ValidationErrorDTO {
    private List<FieldErrorDTO> fieldErrors = new ArrayList<>();
    private HttpStatusCode httpStatusCode;
    private String message;

    public ValidationErrorDTO() {
        super();
    }

    public void addFieldError(String errorCode, String message, String field) {
        FieldErrorDTO error = new FieldErrorDTO(errorCode, message, field);
        fieldErrors.add(error);
    }

}
