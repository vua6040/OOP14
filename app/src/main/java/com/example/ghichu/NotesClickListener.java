package com.example.ghichu;

import androidx.cardview.widget.CardView;

import com.example.ghichu.models.NoteModel;

public interface NotesClickListener {
    void onClick(NoteModel notes);
    void onLongClick(NoteModel notes, CardView cardView);

}
