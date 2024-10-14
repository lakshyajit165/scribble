package com.scribble.noteservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateNoteDTO {

    private String title;
    @NotBlank(message = "Description is required")
    @Size(max = 300, message = "Description must be 300 characters or less")
    private String description;
    private String label;
    private Date dueDate;

}

