package com.scribble.noteservice.service;

import com.scribble.noteservice.dto.CreateNoteDTO;
import com.scribble.noteservice.dto.UpdateNoteDTO;
import com.scribble.noteservice.exception.AccessDeniedException;
import com.scribble.noteservice.exception.ResourceNotFoundException;
import com.scribble.noteservice.model.Note;
import com.scribble.noteservice.repository.NotesRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class NotesService {

    @Autowired
    private NotesRepository notesRepository;

    // create a note
    public Note createNote(CreateNoteDTO createNoteDTO, Authentication authentication) {
        Note note = new Note();
        note.setTitle(createNoteDTO.getTitle());
        note.setDescription(createNoteDTO.getDescription());
        note.setAuthor(authentication.getName());
        note.setLabel(createNoteDTO.getLabel());
        note.setNoteType(createNoteDTO.getNoteType());
        note.setIsComplete(createNoteDTO.getIsComplete());
        return notesRepository.save(note);
    }

    // get note by id
    public Note getNoteById(Long noteId, Authentication authentication)
            throws ResourceNotFoundException, AccessDeniedException {
        if(!notesRepository.existsById(noteId))
            throw new ResourceNotFoundException("Note", "id", noteId);

        // check if user trying to access the note actually created the note
        Note note = notesRepository.getReferenceById(noteId);
        if(!authentication.getName().equals(note.getAuthor()))
            throw new AccessDeniedException("Read", "note");
        return notesRepository.findById(noteId).get();

    }

    // edit note
    public Note updateNote(
            UpdateNoteDTO updateNoteDTO,
            Long noteId, Authentication authentication)
            throws ResourceNotFoundException, AccessDeniedException {
        if(!notesRepository.existsById(noteId))
            throw new ResourceNotFoundException("Note", "id", noteId);

        Note note = notesRepository.getReferenceById(noteId); // already checked if note exists in if
        // check if the user trying to update the note, actually created the note
        if(!authentication.getName().equals(note.getAuthor())){
            throw new AccessDeniedException("Write", "Note");
        }
        Instant now = Instant.now();
        note.setUpdatedAt(now);
        note.setTitle(updateNoteDTO.getTitle() == null
                || updateNoteDTO.getTitle().isBlank() ? note.getTitle() : updateNoteDTO.getTitle());
        note.setDescription(updateNoteDTO.getDescription() == null
                || updateNoteDTO.getDescription().isBlank() ? note.getDescription() : updateNoteDTO.getDescription());
        note.setLabel(updateNoteDTO.getLabel() == null
                || updateNoteDTO.getLabel().isBlank() ? note.getLabel() : updateNoteDTO.getLabel());
        note.setNoteType(updateNoteDTO.getNoteType() == null
                || updateNoteDTO.getNoteType().toString().isBlank() ? note.getNoteType() : updateNoteDTO.getNoteType());
        note.setIsComplete(updateNoteDTO.getIsComplete());
        return notesRepository.save(note);

    }

    // delete note
    public void deleteNote(Long noteId, Authentication authentication)
            throws ResourceNotFoundException, AccessDeniedException {
        if(!notesRepository.existsById(noteId)){
            throw new ResourceNotFoundException("Note", "id", noteId);
        }
        Note note = notesRepository.getReferenceById(noteId);
        // check if user trying to delete the note actually created it
        if(!authentication.getName().equals(note.getAuthor())){
            throw new AccessDeniedException("Delete", "Note");
        }
        notesRepository.deleteById(noteId);
    }

}
