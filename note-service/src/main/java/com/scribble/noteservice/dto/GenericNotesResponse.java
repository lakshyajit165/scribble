package com.scribble.noteservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.scribble.noteservice.model.HttpStatusCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GenericNotesResponse<T> {
    private String message;

    private T data;

    public GenericNotesResponse(String message) {
        this.message = message;
    }
}
