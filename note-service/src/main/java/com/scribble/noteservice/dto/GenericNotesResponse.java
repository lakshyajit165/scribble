package com.scribble.noteservice.dto;

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
