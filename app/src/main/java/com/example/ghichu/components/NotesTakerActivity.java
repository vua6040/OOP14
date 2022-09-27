package com.example.ghichu.components;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.ghichu.MainActivity;
import com.example.ghichu.models.Notes;
import com.example.ghichu.R;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class NotesTakerActivity extends AppCompatActivity {

    private static final int MY_REQUEST_CODE = 10;

    public static final String TAG=Manifest.class.getName();
    TextView textView_select,textView_take_photo;
    CardView cardView,model_card;
    EditText editText_title,editText_notes;
    ImageView imageView_back,imageView_img;
    Uri mUri;
    ImageButton imageView_pin,imageView_timer,imageView_save;
    Notes notes;
    boolean isOldNote=false;

    ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),(result)->{
                Log.e(TAG,"onActivityResult");
                if(result.getResultCode()==Activity.RESULT_OK){
                    Intent data= result.getData();

                    if(data==null){
                        return;
                    }

                    Uri uri=data.getData();
                    mUri=uri;
                    try{
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                        imageView_img.setImageBitmap(bitmap);
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }

            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_taker);

        imageView_save = findViewById(R.id.imageView_save);
        editText_notes = findViewById(R.id.editText_notes);
        editText_title = findViewById(R.id.editText_title);
        imageView_back = findViewById(R.id.imageView_back);

        cardView = findViewById(R.id.cardView);
        model_card = findViewById(R.id.model_card);
        textView_select = findViewById(R.id.textView_select);
        textView_take_photo = findViewById(R.id.textView_take_photo);

        imageView_img = findViewById(R.id.imageView_img);

        //EDITTING NOTE OLD
        notes = new Notes();
        try {
            notes = (Notes) getIntent().getSerializableExtra("noteOld");
            if(!(notes.getTitle().length() >0) && !notes.getTitle().isEmpty()){
                editText_title.setText(notes.getTitle());
                editText_notes.setText(notes.getNotes());
            }else{
                editText_title.setText("");
                editText_notes.setText("");
            }
//            imageView_img.setImageResource(notes.getImg()); still error
            isOldNote=true;
        }catch (Exception ex){
            Toast.makeText(NotesTakerActivity.this, ex.toString(),
                    Toast.LENGTH_SHORT).show();
        }

        //SAVE DATA
        imageView_save.setOnClickListener(view -> {
            String title = editText_title.getText().toString();
            String description = editText_notes.getText().toString();
//                String img = imageView_img.getResources().toString();

            if(description.isEmpty()||title.isEmpty()){
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
        });

        //BACK HOME
        imageView_back.setOnClickListener(view -> {
            Intent intent = new Intent(NotesTakerActivity.this,MainActivity.class);
            startActivity(intent);
        });

        //SELECT IMAGE
        textView_select.setOnClickListener(view -> onClickRequestPermission());

        textView_take_photo.setOnClickListener(this::onClick);
    }

    private void onClickRequestPermission() {
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            openGallery();
            return;
        }

        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
            openGallery();
        }else{
            String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermissions(permission,MY_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==MY_REQUEST_CODE){
            if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                openGallery();
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mActivityResultLauncher.launch(Intent.createChooser(intent,"Select Picture"));
    }

    public void adds(View view){
        cardView.setVisibility(View.VISIBLE);
        model_card.animate().translationY(1460).setDuration(500).setStartDelay(500);
    }

    public void picture(View view){
        int visible=cardView.getVisibility();
        if(visible==4)
            cardView.setVisibility(View.VISIBLE);
        else {
            cardView.setVisibility(View.INVISIBLE);
            model_card.setTranslationY(2000);
        }
    }

    public void select_take_photo(View view){
        Intent take = new Intent(NotesTakerActivity.this, CameraPicture.class);
        startActivity(take);
    }

    public void select_drawing(View view){
        Intent i = new Intent(NotesTakerActivity.this,DrawingActivity.class);
        startActivity(i);
    }

    private void onClick(View view) {
        Intent i = new Intent(NotesTakerActivity.this, CameraPicture.class);
        startActivity(i);
    }
}