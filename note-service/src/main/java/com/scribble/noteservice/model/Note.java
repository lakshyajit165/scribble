package com.scribble.noteservice.model;

import com.scribble.noteservice.model.DateAudit.DateAudit;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;
import javax.ws.rs.DefaultValue;

@Entity
@Table(name = "notes")
@Getter
@Setter
public class Note extends DateAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Default value setting not working somehow in JPA :( (Set through request DTO)
    private String title;

    @NotBlank
    private String description;

    @NotBlank
    @Email
    private String author;

    @Column(nullable = true)
    private String label;

    @Enumerated(EnumType.STRING)
    private NoteType noteType;

    @NotNull
    private Boolean isComplete;

    // @NotBlank for string, @NotNull for others
}
