package com.scribble.noteservice.dto;

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
public class CreateNoteDTO {

    // Default value taken as "Untitled"
    private String title="Untitled";

    @NotBlank(message = "Description is required")
    private String description;

    // label might be empty
    private String label;

    @NotNull(message = "Note type is required")
    private NoteType noteType;

    @NotNull(message = "Completion status is required")
    private Boolean isComplete;

}
