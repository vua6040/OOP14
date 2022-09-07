package com.example.ghichu;

import androidx.cardview.widget.CardView;

import com.example.ghichu.Models.Notes;

public interface NotesClickListener {
    void onClick(Notes notes);
    void onLongClick(Notes notes, CardView cardView);
}
