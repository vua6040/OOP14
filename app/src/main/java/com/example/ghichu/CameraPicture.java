package com.example.ghichu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ghichu.components.NotesTakerActivity;
import com.google.firebase.storage.FirebaseStorage;

import java.io.ByteArrayOutputStream;


public class CameraPicture extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = Manifest.class.getName();

    public Uri sImage;
    private ImageButton imageView_back, imageView_capture, imageView_save;
    private ImageView previewView;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_picture);

        storage = FirebaseStorage.getInstance();
        imageView_capture = findViewById(R.id.imageView_capture);
        imageView_back = findViewById(R.id.imageView_back);
        previewView = findViewById(R.id.previewView);

        imageView_save = findViewById(R.id.imageView_save);
        imageView_save.setOnClickListener(this);


        imageView_back.setOnClickListener(view -> {
            Intent i = new Intent(CameraPicture.this, NotesTakerActivity.class);
            startActivity(i);
        });

        imageView_capture.setOnClickListener(view -> {
            Intent iCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityIfNeeded(iCamera, 2002);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2002 && resultCode == RESULT_OK) {
            Log.e(TAG, "onActivityResult");
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), bitmap, "val", null);
            sImage = Uri.parse(path);
            previewView.setImageURI(sImage);
        }
    }

    @Override
    public void onClick(View v) {
        int viewID = v.getId();
        if (viewID == R.id.imageView_save) {
            if (sImage != null) {
                Intent intent = new Intent(CameraPicture.this, NotesTakerActivity.class);
                intent.putExtra("takePicture", String.valueOf(sImage));
                startActivity(intent);
            } else {
                Toast.makeText(CameraPicture.this, "You haven't photographed him yet", Toast.LENGTH_LONG).show();
            }
        }
    }
}