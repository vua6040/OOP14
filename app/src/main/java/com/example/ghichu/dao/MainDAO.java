package com.example.ghichu.dao;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.ghichu.models.Notes;

import java.util.List;

@Dao
public interface MainDAO {
    @Insert(onConflict = REPLACE)
    void insertNote(Notes... notes);

    @Query("SELECT * FROM notes ORDER BY id DESC")
    List<Notes> getAll();

    @Query("UPDATE notes SET title=:title ,notes=:notes,img=:img WHERE id=:Id")
    void updateNote(int Id,String title,String notes,String img);

    @Delete
    void deleteNote(Notes notes);

    @Query("UPDATE notes SET pinned=:pin WHERE id=:id")
    void pin(int id,boolean pin);
}
