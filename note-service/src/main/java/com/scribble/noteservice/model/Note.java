package com.scribble.noteservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.scribble.noteservice.model.DateAudit.DateAudit;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;

@Entity
@Table(name = "notes")
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // source: https://stackoverflow.com/questions/67353793/what-does-jsonignorepropertieshibernatelazyinitializer-handler-do
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

    @Column(nullable = true)
    private Instant dueDate;

    // @NotBlank for string, @NotNull for others
}
