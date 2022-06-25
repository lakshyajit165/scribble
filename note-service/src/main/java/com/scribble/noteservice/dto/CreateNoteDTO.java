package com.scribble.noteservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.Date;

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

    // due date might be empty
    private Date dueDate;

}
