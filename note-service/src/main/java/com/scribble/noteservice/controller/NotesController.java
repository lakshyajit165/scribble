package com.scribble.noteservice.controller;

import com.scribble.noteservice.constants.AppConstants;
import com.scribble.noteservice.dto.*;
import com.scribble.noteservice.exception.AccessDeniedException;
import com.scribble.noteservice.exception.BadRequestException;
import com.scribble.noteservice.exception.ResourceNotFoundException;
import com.scribble.noteservice.model.Note;
import com.scribble.noteservice.service.NotesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.web.context.request.WebRequest;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/v1/notes")
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

    @GetMapping("/search")
    public ResponseEntity<?> getNotes(
            @RequestParam(value = "searchText", required = false) String searchText,
            @RequestParam(value = "updatedOnOrAfter", required = false)
                String updatedOnOrAfter,
            @RequestParam(value = "updatedOnOrBefore", required = false)
                String updatedOnOrBefore,
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            Authentication authentication){
        try {
            String fromDate = updatedOnOrAfter != null && !updatedOnOrAfter.isBlank() ? updatedOnOrAfter + "T00:00:00Z" : null;
            String toDate = updatedOnOrBefore != null && !updatedOnOrBefore.isBlank() ? updatedOnOrBefore + "T23:59:59Z" : null;
            return ResponseEntity.status(200).body(notesService.getNotes(searchText, fromDate, toDate, page, size, authentication));
        } catch (BadRequestException e) {
            logger.error("Error fetching notes: " + e.getMessage());
            return ResponseEntity.status(400).body(
                    new PaginatedResponse(
                            Collections.<Note>emptyList(),
                            e.getMessage(),
                            page, size,
                            0, 0, true));
        } catch (Exception e) {
            logger.error("Error fetching notes: " + e.getMessage());
            return ResponseEntity.status(500).body(
                    new PaginatedResponse(
                            Collections.<Note>emptyList(),
                            "Error fetching notes",
                            page, size,
                            0, 0, true));
        }

    }

    @PostMapping("/create_note")
    public ResponseEntity<?> createNote(@Valid @RequestBody CreateNoteDTO createNoteDTO, Authentication authentication){
        logger.info("create note called");
        try {
            Note note = notesService.createNote(createNoteDTO, authentication);
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
        } catch (AccessDeniedException e) {
            logger.error("Access denied to delete note by user! " + e.getMessage());
            return ResponseEntity.status(403).body(new GenericNotesResponse<>(e.getMessage()));
        } catch(ResourceNotFoundException e) {
            logger.error("Note with id %s not found while fetching note! " + e.getMessage(), noteId);
            return ResponseEntity.status(404).body(new GenericNotesResponse<>(e.getMessage()));
        } catch(Exception e) {
            logger.error("Error fetching note with id %s " + e.getMessage(), noteId);
            return ResponseEntity.status(500).body(new GenericNotesResponse<>("Error fetching note!"));
        }
    }

    @PatchMapping("/update_note/{noteId}")
    public ResponseEntity<?> updateNoteById(
            @RequestBody UpdateNoteDTO updateNoteDTO,
            @PathVariable("noteId") String noteId,
            Authentication authentication
    ){
        try{
            if(noteId.matches("[0-9]+")){
                Note note = notesService.updateNote(updateNoteDTO, Long.parseLong(noteId), authentication);
                return ResponseEntity.status(200).body(new GenericNotesResponse<>("Note updated!", note));
            }
            return ResponseEntity.status(400).body(new GenericNotesResponse<>("Invalid note id!"));
        } catch (AccessDeniedException e) {
            logger.error("Access denied to delete note by user! " + e.getMessage());
            return ResponseEntity.status(403).body(new GenericNotesResponse<>(e.getMessage()));
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
        } catch (AccessDeniedException e) {
            logger.error("Access denied to delete note by user! " + e.getMessage());
            return ResponseEntity.status(403).body(new GenericNotesResponse<>(e.getMessage()));
        } catch (ResourceNotFoundException e) {
            logger.error("Note with id %s not found while deleting note! " + e.getMessage(), noteId);
            return ResponseEntity.status(404).body(new GenericNotesResponse<>(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error deleting note with id %s " + e.getMessage(), noteId);
            return ResponseEntity.status(500).body(new GenericNotesResponse<>("Error deleting note!"));
        }
    }

}
