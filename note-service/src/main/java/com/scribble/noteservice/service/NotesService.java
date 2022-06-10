package com.scribble.noteservice.service;

import com.scribble.noteservice.dto.CreateOrUpdateNoteDTO;
import com.scribble.noteservice.exception.AccessDeniedException;
import com.scribble.noteservice.exception.ResourceNotFoundException;
import com.scribble.noteservice.model.Note;
import com.scribble.noteservice.repository.NotesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class NotesService {

    @Autowired
    private NotesRepository notesRepository;

    // create a note
    public Note createNote(CreateOrUpdateNoteDTO createOrUpdateNoteDTO, Authentication authentication) {
        Note note = new Note();
        note.setTitle(createOrUpdateNoteDTO.getTitle());
        note.setDescription(createOrUpdateNoteDTO.getDescription());
        note.setAuthor(authentication.getName());
        note.setLabel(createOrUpdateNoteDTO.getLabel());
        note.setNoteType(createOrUpdateNoteDTO.getNoteType());
        note.setIsComplete(createOrUpdateNoteDTO.getIsComplete());
        return notesRepository.save(note);
    }

    // get note by id
    public Note getNoteById(Long noteId, Authentication authentication)
            throws ResourceNotFoundException, AccessDeniedException {
        if(notesRepository.existsById(noteId)) {
            // check if user trying to access the note actually created the note
            Note note = notesRepository.getReferenceById(noteId);
            if(!authentication.getName().equals(note.getAuthor()))
                throw new AccessDeniedException("Read", "note");
            return notesRepository.findById(noteId).get();
        }
        throw new ResourceNotFoundException("Note", "id", noteId);
    }

    // edit note
    public Note updateNote(
            CreateOrUpdateNoteDTO createOrUpdateNoteDTO,
            Long noteId, Authentication authentication)
            throws ResourceNotFoundException, AccessDeniedException {
        if(notesRepository.existsById(noteId)){
            Note note = notesRepository.getReferenceById(noteId); // already checked if note exists in if
            // check if the user trying to update the note, actually created the note
            if(!authentication.getName().equals(note.getAuthor())){
                throw new AccessDeniedException("Write", "Note");
            }
            Instant now = Instant.now();
            note.setUpdatedAt(now);
            note.setTitle(createOrUpdateNoteDTO.getTitle());
            note.setDescription(createOrUpdateNoteDTO.getDescription());
            note.setNoteType(createOrUpdateNoteDTO.getNoteType());
            note.setLabel(createOrUpdateNoteDTO.getLabel());
            note.setIsComplete(createOrUpdateNoteDTO.getIsComplete());
            return notesRepository.save(note);
        }
        throw new ResourceNotFoundException("Note", "id", noteId);
    }

    // delete note
    public void deleteNote(Long noteId, Authentication authentication)
            throws ResourceNotFoundException, AccessDeniedException {
        if(notesRepository.existsById(noteId)){
            Note note = notesRepository.getReferenceById(noteId);
            // check if user trying to delete the note actually created it
            if(!authentication.getName().equals(note.getAuthor())){
                throw new AccessDeniedException("Delete", "Note");
            }
            notesRepository.deleteById(noteId);
        }
        throw new ResourceNotFoundException("Note", "id", noteId);
    }

}
