package com.scribble.noteservice.dto;

import com.scribble.noteservice.model.DateAudit.DateAudit;
import com.scribble.noteservice.model.NoteType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateNoteDTO {

    private String title;
    private String description;
    private String label;
    private NoteType noteType;
    private Boolean isComplete;

}

