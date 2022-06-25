package com.scribble.noteservice.controller;

import com.scribble.noteservice.dto.CreateNoteDTO;
import com.scribble.noteservice.dto.GenericNotesResponse;
import com.scribble.noteservice.dto.UpdateNoteDTO;
import com.scribble.noteservice.exception.AccessDeniedException;
import com.scribble.noteservice.exception.ResourceNotFoundException;
import com.scribble.noteservice.model.Note;
import com.scribble.noteservice.service.NotesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/note-service/api/v1/notes")
public class NotesController {

    @Autowired
    private NotesService notesService;

    private static final Logger logger = LoggerFactory.getLogger(NotesController.class);

    @GetMapping("/test")
    public ResponseEntity<?> getTestResponse(
            @RequestParam(value = "startDate") String startDate,
            @RequestParam(value = "endDate") String endDate) {
        logger.info(startDate);
        logger.info(endDate);
        return ResponseEntity.status(200).body(new GenericNotesResponse<>("Notes service is up!"));
    }

    @GetMapping("/notes_resource")
    public ResponseEntity<?> getProtectedResource() {
        return ResponseEntity.status(200).body(new GenericNotesResponse<>("Access granted for notes service!"));
    }

    @GetMapping("/search")
    public List<Note> getNotes(
            @RequestParam(value = "text", required = false) String text,
            @RequestParam(value = "updatedOnOrAfter", required = false)
                String updatedOnOrAfter,
            @RequestParam(value = "updatedOnOrBefore", required = false)
                String updatedOnOrBefore,
            Authentication authentication){

        String fromDate = updatedOnOrAfter != null ? updatedOnOrAfter + "T00:00:00Z" : null;
        String toDate = updatedOnOrBefore != null ? updatedOnOrBefore + "T23:59:59Z" : null;
        return notesService.getNotes(text, fromDate, toDate);

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
                return ResponseEntity.status(200).body(new GenericNotesResponse<>("Note udpated!", note));
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
