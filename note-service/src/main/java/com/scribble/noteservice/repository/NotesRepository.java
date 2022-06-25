package com.scribble.noteservice.repository;

import com.scribble.noteservice.model.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;

@Repository
public interface NotesRepository extends JpaRepository<Note, Long>, JpaSpecificationExecutor<Note>{

    // get notes by a particular person
//    @Query(value = "SELECT n FROM NOTES n WHERE AUTHOR = :author")
//    Page<Note> findByAuthor(@Param("author") String author, Pageable pageable);



}
