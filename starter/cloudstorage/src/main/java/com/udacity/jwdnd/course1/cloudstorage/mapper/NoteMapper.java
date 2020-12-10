package com.udacity.jwdnd.course1.cloudstorage.mapper;

import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface NoteMapper {

    @Select("SELECT * FROM notes")
    List<Note> findAll();

    @Select("SELECT * FROM notes WHERE userid = #{userId}")
    List<Note> findByUserId(Integer userId);

    @Select("SELECT * FROM notes WHERE noteid = #{noteId}")
    Note findById(Integer noteId);

    @Insert("INSERT INTO notes (notetitle, notedescription, userid) "
            + "VALUES(#{noteTitle}, #{noteDescription}, #{userId})")
    @Options(useGeneratedKeys = true, keyProperty = "noteId")
    int insert(Note note);

    @Update("UPDATE notes "
            + "SET notetitle = #{title}, notedescription = #{description} "
            + "WHERE noteid = #{noteId}")
    int update(Integer noteId, String title, String description);

    @Delete("DELETE FROM notes WHERE noteid = #{noteId}")
    int delete(Integer noteId);
}
