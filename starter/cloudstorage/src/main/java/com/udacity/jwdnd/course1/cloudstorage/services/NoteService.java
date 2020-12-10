package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mapper.NoteMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class NoteService {

    private final NoteMapper noteMapper;

    public NoteService(NoteMapper noteMapper) {
        this.noteMapper = noteMapper;
    }

    public List<Note> getNotesByUserId(Integer userId) {
        return noteMapper.findByUserId(userId);
    }

    public void saveNote(Note note) {
        if (note.getNoteId() == null) {
            noteMapper.insert(note);
        } else {
            noteMapper.update(note.getNoteId(), note.getNoteTitle(), note.getNoteDescription());
        }
    }

    public void deleteNote(Integer noteId) {
        noteMapper.delete(noteId);
    }

}
