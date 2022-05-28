package com.scribble.noteservice.dto;

import com.scribble.noteservice.model.NotesResponseType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GenericNotesResponse {
    private NotesResponseType notesResponseType;
    private String message;
}
