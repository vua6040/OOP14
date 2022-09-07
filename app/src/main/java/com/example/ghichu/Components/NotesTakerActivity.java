package com.example.ghichu.Components;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ghichu.MainActivity;
import com.example.ghichu.Models.Notes;
import com.example.ghichu.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NotesTakerActivity extends AppCompatActivity {

    EditText editText_title,editText_notes;
    ImageView imageView_back,imageView_img;
    ImageButton imageView_pin,imageView_timer,imageView_save;
    Notes notes;
    boolean isOldNote=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_taker);

        imageView_save = findViewById(R.id.imageView_save);
        editText_notes = findViewById(R.id.editText_notes);
        editText_title = findViewById(R.id.editText_title);
        imageView_back = findViewById(R.id.imageView_back);

//        imageView_img = findViewById(R.id.imageView_img);

        //EDITTING NOTE OLD
        notes = new Notes();
        try {
            notes = (Notes) getIntent().getSerializableExtra("noteOld");
            editText_title.setText(notes.getTitle());
            editText_notes.setText(notes.getNotes());
//            imageView_img.setImageResource(notes.getImg()); still error
            isOldNote=true;
        }catch (Exception ex){
            Toast.makeText(NotesTakerActivity.this, ex.toString(),
                    Toast.LENGTH_SHORT).show();
        }

        //SAVE DATA
        imageView_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = editText_title.getText().toString();
                String description = editText_notes.getText().toString();
//                String img = imageView_img.getResources().toString();

                if(description.isEmpty()){
                    Toast.makeText(NotesTakerActivity.this,"Please add some notes!",Toast.LENGTH_LONG);
                    return;
                }

                SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm a");
                Date date = new Date();

                if(!isOldNote) notes = new Notes();
                notes.setTitle(title);
                notes.setNotes(description);
//                notes.setImg(img);
                notes.setDate(formatter.format(date));

                Intent intent = new Intent();
                intent.putExtra("newNote",notes);
                setResult(Activity.RESULT_OK,intent);
                finish();
            }

        });

        //BACK HOME
        imageView_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NotesTakerActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }
}