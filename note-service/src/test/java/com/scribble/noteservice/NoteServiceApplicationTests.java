package com.scribble.noteservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scribble.noteservice.constants.AppConstants;
import com.scribble.noteservice.dto.CreateNoteDTO;
import com.scribble.noteservice.dto.UpdateNoteDTO;
import com.scribble.noteservice.model.Note;
import com.scribble.noteservice.repository.NotesRepository;
import com.scribble.noteservice.service.NotesService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.Instant;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@SpringBootTest
@AutoConfigureMockMvc
class NoteServiceApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private NotesService notesService;

	@Autowired
	private NotesRepository notesRepository;

	@Autowired
	private ObjectMapper objectMapper;

	private int noOfSavedNotes = 0;

	/**
	 * Success case for create note
	 * */
	@Test
	@WithMockUser(username = "user1@test.com", password = "pwd")
	public void createNoteSuccess() throws Exception {
		CreateNoteDTO createNoteDTO = new CreateNoteDTO("Test title", "Test Description", "Test label", new Date());
		ResultActions response = mockMvc.perform(post("/api/v1/notes/create_note")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createNoteDTO)));
		response.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.message").exists())
				.andExpect(jsonPath("$.message").value("Note created!"))
				.andExpect(jsonPath("$.data.title").exists())
				.andExpect(jsonPath("$.data.title").value(createNoteDTO.getTitle()))
				.andExpect(jsonPath("$.data.description").exists())
				.andExpect(jsonPath("$.data.description").value(createNoteDTO.getDescription()))
				.andExpect(jsonPath("$.data.author").exists())
				.andExpect(jsonPath("$.data.author").value("user1@test.com"))
				.andExpect(jsonPath("$.data.label").exists())
				.andExpect(jsonPath("$.data.label").value(createNoteDTO.getLabel()));
	}

	/**
	 * Failure case for create note
	 * */
	@Test
	@WithMockUser(username = "user1@test.com", password = "pwd")
	public void createNoteFailure() throws Exception {
		// creating note with empty description
		CreateNoteDTO createNoteDTO = new CreateNoteDTO("Test title", "", "Test label", new Date());
		ResultActions response = mockMvc.perform(post("/api/v1/notes/create_note")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createNoteDTO)));
		response.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").exists())
				.andExpect(jsonPath("$.message").value("There are validation errors"))
				.andExpect(jsonPath("$.errors").exists())
				.andExpect(jsonPath("$.errors[0]").value("Description is required"));
	}

	/**
	 * Success case for get note by id
	 * */
	@Test
	@WithMockUser(username = "user1@test.com", password = "pwd")
	public void getNoteByIdSuccess() throws Exception {
		Note savedNote = saveNote();
		ResultActions response = mockMvc.perform(get("/api/v1/notes/get_note/{noteId}", savedNote.getId())
				.contentType(MediaType.APPLICATION_JSON));
		response.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").exists())
				.andExpect(jsonPath("$.message").value("Note found!"));
	}

	/**
	 * Failure case for get note by id (invalid id)
	 * */
	@Test
	@WithMockUser(username = "user1@test.com", password = "pwd")
	public void getNoteByIdFailureWithInvalidId() throws Exception {
		ResultActions response = mockMvc.perform(get("/api/v1/notes/get_note/{noteId}", "abcd12")
				.contentType(MediaType.APPLICATION_JSON));
		response.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").exists())
				.andExpect(jsonPath("$.message").value("Invalid note id!"));
	}

	/**
	 * Failure case for get note by id (note with id does not exist)
	 * */
	@Test
	@WithMockUser(username = "user1@test.com", password = "pwd")
	public void getNoteByIdFailureWhereNoteIdDoesNotExist() throws Exception {
		ResultActions response = mockMvc.perform(get("/api/v1/notes/get_note/{noteId}", "1223876")
				.contentType(MediaType.APPLICATION_JSON));
		response.andDo(print())
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").exists())
				.andExpect(jsonPath("$.message").value("No Note found with id 1223876"));
	}

	/**
	 * Success case for update note
	 * */
	@Test
	@WithMockUser(username = "user1@test.com", password = "pwd")
	public void updateNoteSuccess() throws Exception {
		Note savedNote = saveNote();
		UpdateNoteDTO updateNoteDTO = new UpdateNoteDTO(savedNote.getTitle(), "Test Description updated", savedNote.getLabel(), Date.from(savedNote.getDueDate() != null ? savedNote.getDueDate() : Instant.now()));
		ResultActions response = mockMvc.perform(patch("/api/v1/notes/update_note/{noteId}", savedNote.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateNoteDTO)));
		response.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").exists())
				.andExpect(jsonPath("$.message").value("Note updated!"))
				.andExpect(jsonPath("$.data.description").exists())
				.andExpect(jsonPath("$.data.description").value(updateNoteDTO.getDescription()));
	}

	/**
	 * Failure case for update note (invalid id)
	 * */
	@Test
	@WithMockUser(username = "user1@test.com", password = "pwd")
	public void updateNoteFailureWithInvalidId() throws Exception {
		UpdateNoteDTO updateNoteDTO = new UpdateNoteDTO("Test title", "Test Description updated", "Test label", Date.from(Instant.now()));
		ResultActions response = mockMvc.perform(patch("/api/v1/notes/update_note/{noteId}", "pqrs")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateNoteDTO)));
		response.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").exists())
				.andExpect(jsonPath("$.message").value("Invalid note id!"));
	}

	/**
	 * Failure case for update note (Note id does not exist)
	 * */
	@Test
	@WithMockUser(username = "user1@test.com", password = "pwd")
	public void updateNoteFailureWhereNoteIdDoesNotExist() throws Exception {
		UpdateNoteDTO updateNoteDTO = new UpdateNoteDTO("Test title", "Test Description updated", "Test label", Date.from(Instant.now()));
		ResultActions response = mockMvc.perform(patch("/api/v1/notes/update_note/{noteId}", "76576565")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateNoteDTO)));
		response.andDo(print())
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").exists())
				.andExpect(jsonPath("$.message").value("No Note found with id 76576565"));
	}

	/**
	 * Failure case for update note (access denied)
	 * */
	@Test
	@WithMockUser(username = "user2@test.com", password = "pwd")
	public void updateNoteFailureWithAccessDenied() throws Exception {
		Note savedNote = saveNote();
		UpdateNoteDTO updateNoteDTO = new UpdateNoteDTO(savedNote.getTitle(), savedNote.getDescription(), savedNote.getLabel(), Date.from(savedNote.getDueDate() != null ? savedNote.getDueDate() : Instant.now()));
		ResultActions response = mockMvc.perform(patch("/api/v1/notes/update_note/{noteId}", saveNote().getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateNoteDTO)));

		response.andDo(print())
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.message").exists())
				.andExpect(jsonPath("$.message").value("Update access denied for resource note"));
	}

	/**
	 * Success case for delete note
	 * */
	@Test
	@WithMockUser(username = "user1@test.com", password = "pwd")
	public void deleteNoteSuccess() throws Exception {
		Note savedNote = saveNote();
		ResultActions response = mockMvc.perform(delete("/api/v1/notes/delete_note/{noteId}", savedNote.getId())
				.contentType(MediaType.APPLICATION_JSON));
		response.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").exists())
				.andExpect(jsonPath("$.message").value("Note deleted!"));
	}

	/**
	 * Failure case for delete note (invalid id)
	 * */
	@Test
	@WithMockUser(username = "user1@test.com", password = "pwd")
	public void deleteNoteFailureWithInvalidId() throws Exception {
		ResultActions response = mockMvc.perform(delete("/api/v1/notes/delete_note/{noteId}", "wxy23")
				.contentType(MediaType.APPLICATION_JSON));
		response.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").exists())
				.andExpect(jsonPath("$.message").value("Invalid note id!"));
	}

	/**
	 * Failure case for delete note (note id doesn't exist)
	 * */
	@Test
	@WithMockUser(username = "user1@test.com", password = "pwd")
	public void deleteNoteFailureWhereNoteIdDoesNotExist() throws Exception {
		ResultActions response = mockMvc.perform(delete("/api/v1/notes/delete_note/{noteId}", "7162537")
				.contentType(MediaType.APPLICATION_JSON));
		response.andDo(print())
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").exists())
				.andExpect(jsonPath("$.message").value("No Note found with id 7162537"));
	}

	/**
	 * Failure case for delete note (access denied)
	 * */
	@Test
	@WithMockUser(username = "user2@test.com", password = "pwd")
	public void deleteNoteFailureWithAccessDenied() throws Exception {
		Note savedNote = saveNote();
		ResultActions response = mockMvc.perform(delete("/api/v1/notes/delete_note/{noteId}", savedNote.getId())
				.contentType(MediaType.APPLICATION_JSON));
		response.andDo(print())
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.message").exists())
				.andExpect(jsonPath("$.message").value("Delete access denied for resource note"));
	}

	/**
	 * Success case for search Notes
	 * */
	@Test
	@WithMockUser(username = "user1@test.com", password = "pwd")
	public void searchNotesSuccess() throws Exception {
		ResultActions response = mockMvc.perform(get("/api/v1/notes/search")
				.contentType(MediaType.APPLICATION_JSON)
				.param("searchText", "test")
				.param("page", String.valueOf(0))
				.param("size", String.valueOf(5)));
		response.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").exists())
				.andExpect(jsonPath("$.message").value("Notes fetched!"))
				.andExpect(jsonPath("$.totalElements").exists())
				/**
				 * 0 => if all of above tests are failures
				 * savedNoOfNotes => above tests are success
				 * (0-savedNoOfNotes) => some of the above tests are failures
				 * */
				.andExpect(jsonPath("$.totalElements", Matchers.allOf(
						Matchers.greaterThanOrEqualTo(0),
						Matchers.lessThanOrEqualTo(getNoOfSavedNotes())
				)));
	}

	/**
	 * Failure case for search Notes (invalid page no)
	 * */
	@Test
	@WithMockUser(username = "user1@test.com", password = "pwd")
	public void searchNotesFailureWithInvalidPageNumber() throws Exception {
		ResultActions response = mockMvc.perform(get("/api/v1/notes/search")
				.contentType(MediaType.APPLICATION_JSON)
				.param("searchText", "test")
				.param("page", String.valueOf(-1))
				.param("size", String.valueOf(5)));
		response.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").exists())
				.andExpect(jsonPath("$.message").value("Page number cannot be less than zero!"));
	}

	/**
	 * Failure case for search Notes (invalid page size)
	 * */
	@Test
	@WithMockUser(username = "user1@test.com", password = "pwd")
	public void searchNotesFailureWithInvalidPageSize() throws Exception {
		ResultActions response = mockMvc.perform(get("/api/v1/notes/search")
				.contentType(MediaType.APPLICATION_JSON)
				.param("searchText", "test")
				.param("page", String.valueOf(1))
				.param("size", String.valueOf(90)));
		response.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").exists())
				.andExpect(jsonPath("$.message").value("Page size must not be greater than " + AppConstants.MAX_PAGE_SIZE + " !"));
	}

	private Note saveNote() {
		Note note = new Note();
		note.setTitle("Test Title");
		note.setDescription("Test Description");
		note.setLabel("Test Label");
		note.setAuthor("user1@test.com");
		note.setCreatedAt(Instant.now());
		note.setUpdatedAt(Instant.now());
		setNoOfSavedNotes();
		return notesRepository.save(note);
	}

	private void setNoOfSavedNotes() {
		this.noOfSavedNotes += 1;
	}

	private int getNoOfSavedNotes() {
		return this.noOfSavedNotes;
	}

}