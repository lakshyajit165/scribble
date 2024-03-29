package com.scribble.noteservice.service;

import com.scribble.noteservice.constants.AppConstants;
import com.scribble.noteservice.dto.CreateNoteDTO;
import com.scribble.noteservice.dto.PaginatedResponse;
import com.scribble.noteservice.dto.UpdateNoteDTO;
import com.scribble.noteservice.exception.AccessDeniedException;
import com.scribble.noteservice.exception.BadRequestException;
import com.scribble.noteservice.exception.ResourceNotFoundException;
import com.scribble.noteservice.model.Note;
import com.scribble.noteservice.repository.NotesRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;


@Service
@Transactional
public class NotesService {

    @Autowired
    private NotesRepository notesRepository;

    private static final Logger logger = LoggerFactory.getLogger(NotesService.class);

    public PaginatedResponse<Note> getNotes(String searchText, String fromDate,
                                      String toDate, int page,
                                      int size, Authentication authentication){
        validatePageNumberAndSize(page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "updatedAt");

        Page<Note> notesPage = notesRepository.findAll(new Specification<Note>() {
            @Override
            public Predicate toPredicate(Root<Note> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(criteriaBuilder.equal(root.get("author"), authentication.getName()));
                if(searchText!=null) {
                    predicates.add(
                            criteriaBuilder.or(
                                    // comparing by converting both the received text and db text to lowercase
                                    criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + searchText.toLowerCase() + "%"),
                                    criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + searchText.toLowerCase() + "%"),
                                    criteriaBuilder.like(criteriaBuilder.lower(root.get("label")), "%" + searchText.toLowerCase() + "%")
                            ));
                }
                if(fromDate != null)
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("updatedAt"), Instant.parse(fromDate)));
                if(toDate != null)
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("updatedAt"), Instant.parse(toDate)));
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, pageable);
        return new PaginatedResponse<Note>(notesPage.getContent(), "Notes fetched!", page, size, notesPage.getTotalElements(), notesPage.getTotalPages(), notesPage.isLast());
    }

    // create a note
    public Note createNote(CreateNoteDTO createNoteDTO, Authentication authentication) {
        Note note = new Note();
        note.setTitle(createNoteDTO.getTitle());
        note.setDescription(createNoteDTO.getDescription());
        note.setAuthor(authentication.getName());
        note.setLabel(createNoteDTO.getLabel() == null
                || createNoteDTO.getLabel().isBlank() ? null : createNoteDTO.getLabel());
        note.setDueDate(createNoteDTO.getDueDate() == null
                || createNoteDTO.getDueDate().toString().isBlank() ? null : createNoteDTO.getDueDate().toInstant());
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

        Note note = notesRepository.findById(noteId).get();// already checked if note exists in if
        // check if the user trying to update the note, actually created the note
        if(!authentication.getName().equals(note.getAuthor())){
            throw new AccessDeniedException("note", "Update");
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
            throw new AccessDeniedException("note", "Delete");
        }
        notesRepository.deleteById(noteId);
    }

    private void validatePageNumberAndSize(int page, int size) {
        if(page < 0)
            throw new BadRequestException("Page number cannot be less than zero!");
        if(size > AppConstants.MAX_PAGE_SIZE)
            throw new BadRequestException("Page size must not be greater than " + AppConstants.MAX_PAGE_SIZE + " !");

    }

}
