package com.scribble.noteservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateNoteDTO {

    private String title;
    private String description;
    private String label;
    private Date dueDate;

}

