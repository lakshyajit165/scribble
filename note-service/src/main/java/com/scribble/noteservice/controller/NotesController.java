package com.scribble.noteservice.controller;

import com.scribble.noteservice.dto.CreateOrUpdateNoteDTO;
import com.scribble.noteservice.dto.GenericNotesResponse;
import com.scribble.noteservice.exception.ResourceNotFoundException;
import com.scribble.noteservice.model.HttpStatusCode;
import com.scribble.noteservice.model.Note;
import com.scribble.noteservice.service.NotesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/note-service/api/v1/notes")
public class NotesController {

    @Autowired
    private NotesService notesService;

    private static final Logger logger = LoggerFactory.getLogger(NotesController.class);

    @GetMapping("/test")
    public ResponseEntity<?> getTestResponse() {
        return ResponseEntity.status(200).body(new GenericNotesResponse<>("Notes service is up!"));
    }

    @GetMapping("/notes_resource")
    public ResponseEntity<?> getProtectedResource() {
        return ResponseEntity.status(200).body(new GenericNotesResponse<>("Access granted for notes service!"));
    }

    @PostMapping("/create_note")
    public ResponseEntity<?> createNote(@Valid @RequestBody CreateOrUpdateNoteDTO createOrUpdateNoteDTO, Authentication authentication){
        try {
            Note note = notesService.createNote(createOrUpdateNoteDTO, authentication);
            return ResponseEntity.status(201).body(new GenericNotesResponse<>("Note created!", note));
        } catch(Exception e) {
            logger.error("Error creating note: " + e.getMessage());
            return ResponseEntity.status(500).body(new GenericNotesResponse<>("Error creating note!"));
        }

    }

    @GetMapping("/get_note/{noteId}")
    public ResponseEntity<?> getNoteById(
            @PathVariable("noteId") String noteId,
            Authentication authentication){
        try {
            if(noteId.matches("[0-9]+")){
                Note note = notesService.getNoteById(Long.parseLong(noteId), authentication);
                return ResponseEntity.status(200).body(new GenericNotesResponse<>("Note found!", note));
            }
            return ResponseEntity.status(400).body(new GenericNotesResponse<>("Invalid note id!"));
        } catch(ResourceNotFoundException e) {
            logger.error("Note with id %s not found while fetching note! " + e.getMessage(), noteId);
            return ResponseEntity.status(404).body(new GenericNotesResponse<>(e.getMessage()));
        } catch(Exception e) {
            logger.error("Error fetching note with id %s " + e.getMessage(), noteId);
            return ResponseEntity.status(500).body(new GenericNotesResponse<>("Error fetching note!"));
        }
    }

    @PutMapping("/update_note/{noteId}")
    public ResponseEntity<?> updateNoteById(
            @Valid @RequestBody CreateOrUpdateNoteDTO createOrUpdateNoteDTO,
            @PathVariable("noteId") String noteId,
            Authentication authentication
    ){
        try{
            if(noteId.matches("[0-9]+")){
                Note note = notesService.updateNote(createOrUpdateNoteDTO, Long.parseLong(noteId), authentication);
                return ResponseEntity.status(200).body(new GenericNotesResponse<>("Note udpated!", note));
            }
            return ResponseEntity.status(400).body(new GenericNotesResponse<>("Invalid note id!"));
        } catch(ResourceNotFoundException e) {
            logger.error("Note with id %s not found while updating note! " + e.getMessage(), noteId);
            return ResponseEntity.status(404).body(new GenericNotesResponse<>(e.getMessage()));
        } catch(Exception e) {
            logger.error("Error updating note with id %s " + e.getMessage(), noteId);
            return ResponseEntity.status(500).body(new GenericNotesResponse<>("Error updating note!"));
        }
    }

    @DeleteMapping("/delete_note/{noteId}")
    public ResponseEntity<?> deleteNoteById(
            @PathVariable("noteId") String noteId,
            Authentication authentication) {
        try {
            if(noteId.matches("[0-9]+")){
                notesService.deleteNote(Long.parseLong(noteId), authentication);
                return ResponseEntity.status(200).body(new GenericNotesResponse<>("Note deleted!"));
            }
            return ResponseEntity.status(400).body(new GenericNotesResponse<>("Invalid note id!"));
        } catch (ResourceNotFoundException e) {
            logger.error("Note with id %s not found while deleting note! " + e.getMessage(), noteId);
            return ResponseEntity.status(404).body(new GenericNotesResponse<>(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error deleting note with id %s " + e.getMessage(), noteId);
            return ResponseEntity.status(500).body(new GenericNotesResponse<>("Error deleting note!"));
        }
    }

}
