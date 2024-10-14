package com.scribble.noteservice.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
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
    @Size(max = 300, message = "Description must be 300 characters or less")
    private String description;

    // label might be empty
    private String label;

    // due date might be empty
    private Date dueDate;

}
