package com.scribble.noteservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.scribble.noteservice.model.DateAudit.DateAudit;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "Description is required")
    @Size(max = 300, message = "Description must be 300 characters or less")
    @Column(length = 300)
    /**
     * Note:
     * The default length for string(VARCHAR) is 255.
     * Looks like this setting can't be changed through hibernate; had to run the following script on pgAdmin to change it:
     * ALTER TABLE notes
     * ALTER COLUMN description TYPE VARCHAR(300);
     * Reference: https://stackoverflow.com/questions/55749780/java-spring-jpa-hibernate-not-updating-column
     * */
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
