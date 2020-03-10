package id.kido1611.mynoteapp.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import id.kido1611.mynoteapp.entity.Note;

@Dao
public interface NoteDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Note note);

    @Query("SELECT * from notes_table order by id desc")
    List<Note> getNotes();
}
