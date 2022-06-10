package com.scribble.noteservice.dto;

import com.scribble.noteservice.model.HttpStatusCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ValidationErrorDTO {
    private List<FieldErrorDTO> fieldErrors = new ArrayList<>();
    private String message;

    public ValidationErrorDTO() {
        super();
    }

    public void addFieldError(String message, String field) {
        FieldErrorDTO error = new FieldErrorDTO(field, message);
        fieldErrors.add(error);
    }

}
