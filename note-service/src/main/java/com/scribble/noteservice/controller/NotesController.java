package com.scribble.noteservice.controller;

import com.scribble.noteservice.dto.GenericNotesResponse;
import com.scribble.noteservice.model.NotesResponseType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/note-service/api/v1/notes")
public class NotesController {

    @GetMapping("/test")
    public ResponseEntity<?> getTestResponse() {
        return ResponseEntity.status(200).body(new GenericNotesResponse(NotesResponseType.SUCCESS, "Notes service is up!"));
    }
}
