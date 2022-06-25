package com.scribble.noteservice.service;

import com.scribble.noteservice.controller.NotesController;
import com.scribble.noteservice.dto.CreateNoteDTO;
import com.scribble.noteservice.dto.UpdateNoteDTO;
import com.scribble.noteservice.exception.AccessDeniedException;
import com.scribble.noteservice.exception.ResourceNotFoundException;
import com.scribble.noteservice.model.Note;
import com.scribble.noteservice.repository.NotesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class NotesService {

    @Autowired
    private NotesRepository notesRepository;

    private static final Logger logger = LoggerFactory.getLogger(NotesService.class);

    public List<Note> getNotes(String text, String fromDate, String toDate, Authentication authentication){
        return notesRepository.findAll(new Specification<Note>() {
            @Override
            public Predicate toPredicate(Root<Note> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(criteriaBuilder.equal(root.get("author"), authentication.getName()));
                if(text!=null) {
                    predicates.add(
                            criteriaBuilder.or(
                                    criteriaBuilder.like(root.get("title"), "%" + text + "%"),
                                    criteriaBuilder.like(root.get("description"), "%" + text + "%"),
                                    criteriaBuilder.like(root.get("label"), "%" + text + "%")
                            ));
                }
                if(fromDate != null && toDate != null){
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("updatedAt"), Instant.parse(fromDate)));
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("updatedAt"), Instant.parse(toDate)));
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        });
    }

    // create a note
    public Note createNote(CreateNoteDTO createNoteDTO, Authentication authentication) {
        Note note = new Note();
        note.setTitle(createNoteDTO.getTitle());
        note.setDescription(createNoteDTO.getDescription());
        note.setAuthor(authentication.getName());
        note.setLabel(createNoteDTO.getLabel());
        note.setDueDate(createNoteDTO.getDueDate().toInstant());
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
        note.setDueDate(updateNoteDTO.getDueDate() == null
                || updateNoteDTO.getDueDate().toString().isBlank() ? note.getDueDate() : updateNoteDTO.getDueDate().toInstant());
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
