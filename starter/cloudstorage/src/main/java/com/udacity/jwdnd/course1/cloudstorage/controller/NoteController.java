package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import com.udacity.jwdnd.course1.cloudstorage.services.NoteService;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class NoteController {

    private final UserService userService;
    private final NoteService noteService;

    public NoteController(UserService userService, NoteService noteService) {
        this.userService = userService;
        this.noteService = noteService;
    }

    @PostMapping("/notes")
    public String createNote(Authentication authentication, @ModelAttribute Note note) {

        String username = (String) authentication.getPrincipal();
        User user = userService.getUser(username);
        if (user == null) {
            return "redirect:/login?logout=Logged+out";
        }

        if (note.getNoteId() == null) {
            note.setUserId(user.getUserId());
        }
        noteService.saveNote(note);
        return "redirect:/result?success=Saved";
    }

    @GetMapping("/delete/notes/{noteId}")
    public String deleteNoteByGet(@PathVariable("noteId") Integer noteId) {
        noteService.deleteNote(noteId);
        return "redirect:/result?success=Deleted";
    }

}
