package com.example.ghichu.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;//Serialization in Java allows us to convert an Object to stream that we can send over the network or save it as file or store in DB for later usage


@Entity(tableName = "notes")
public class Notes implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public int Id=0;

    @ColumnInfo(name = "title",defaultValue = "")
    String title="";

    @ColumnInfo(name = "notes")
    String notes="";

    @ColumnInfo(name = "img")
    String img="";

    @ColumnInfo(name = "date")
    String date="";

    @ColumnInfo(name = "pinned",defaultValue = "false")
    boolean pinned=false;

    public Notes (){

    }

    public Integer getId() {
        return Id;
    }

    public void setId(int id) {
        this.Id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }
}
