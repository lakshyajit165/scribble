package com.scribble.noteservice.dto;

import com.scribble.noteservice.model.HttpStatusCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GenericNotesResponse {
    private HttpStatusCode httpStatusCode;
    private String message;
}
