package com.example.ghichu;

import androidx.cardview.widget.CardView;

import com.example.ghichu.models.Notes;

public interface NotesClickListener {
    void onClick(Notes notes);
    void onLongClick(Notes notes, CardView cardView);
}
