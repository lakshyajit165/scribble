package com.scribble.noteservice.dto;

import com.scribble.noteservice.model.NoteType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DefaultValue;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrUpdateNoteDTO {

    // Default value taken as "Untitled"
    private String title="Untitled";

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Label is required")
    private String label;

    @NotNull(message = "Note type is required")
    private NoteType noteType;

    @NotNull(message = "Completion status is required")
    private Boolean isComplete;

}
