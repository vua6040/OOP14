package com.example.ghichu.components;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.ghichu.CameraPicture;
import com.example.ghichu.MainActivity;
import com.example.ghichu.api.ApiService;
import com.example.ghichu.models.NoteModel;
import com.example.ghichu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NotesTakerActivity extends AppCompatActivity {

    private static final int MY_REQUEST_CODE = 10;

    public static final String TAG=Manifest.class.getName();
    private TextView textView_select,textView_take_photo;
    private CardView cardView,model_card;
    private EditText editText_title,editText_notes;
    private ImageView imageView_back,imageView_img;
    private  Uri sImage;
    private ImageButton imageView_pin,imageView_timer,imageView_save;
    FirebaseStorage storage;
    NoteModel noteModel;

    private boolean isOldNote=false;
    private boolean pinned=false;

    private ProgressDialog mProgressDialog;

    ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),(result)->{
                Log.e(TAG,"onActivityResult");
                if(result.getResultCode()==Activity.RESULT_OK){
                    Intent data= result.getData();

                    if(data==null){
                        return;
                    }

                    Uri uri=data.getData();
                    sImage = uri;
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
        imageView_pin = findViewById(R.id.imageView_pin);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Please wait ...");

        //FIREBASE
        storage = FirebaseStorage.getInstance();


        //EDITTING NOTE OLD
        noteModel = new NoteModel();
        try {
            int idNote = (int) getIntent().getSerializableExtra("noteOld");
                ApiService.apiService.getNote(idNote).enqueue(new Callback<NoteModel>() {
                    @Override
                    public void onResponse(Call<NoteModel> call, Response<NoteModel> response) {
                        noteModel = response.body();
                        editText_title.setText(noteModel.getTitle());
                        editText_notes.setText(noteModel.getNotes());
                        StorageReference storageReference = storage.getReferenceFromUrl("gs://ghi-chu-8944e.appspot.com/images/").child(noteModel.getImg());
                        try {
                            final File file = File.createTempFile("image","jpg");
                            storageReference.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                                    imageView_img.setImageBitmap(bitmap);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(NotesTakerActivity.this,"Image faild to load",Toast.LENGTH_LONG).show();
                                }
                            });

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<NoteModel> call, Throwable t) {
                        Toast.makeText(NotesTakerActivity.this, t.toString(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            isOldNote=true;
        }catch (Exception ex){
            Log.e("Note Old","not selected yet");
        }

        //CLICK PINNED
        imageView_pin.setOnClickListener(view -> {
            if(pinned){
                pinned = false;
                Toast.makeText(NotesTakerActivity.this,"Cancel Pinned",Toast.LENGTH_LONG).show();
            }else{
                pinned = true;
                Toast.makeText(NotesTakerActivity.this,"Pinned",Toast.LENGTH_LONG).show();
            }
        });

        //SAVE DATA
        imageView_save.setOnClickListener(view -> {
            mProgressDialog.show();
            String title = editText_title.getText().toString().trim();
            String description = editText_notes.getText().toString().trim();

            SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm a");
            Date date = new Date();

            if(!isOldNote) noteModel = new NoteModel();

            //handle image save in firebase
            if(sImage!=null){
                String rId=UUID.randomUUID().toString();
                StorageReference reference = storage.getReference().child("images/"+rId );
                reference.putFile(sImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            Log.d("upload","success");
                        }else{
                            Log.e("upload","fail");
                        }
                    }
                });
                noteModel.setImg(rId);
            }

            if(pinned) noteModel.setPinned(true);

            noteModel.setTitle(title);
            noteModel.setTimeCreate(formatter.format(date));
            noteModel.setNotes(description);

            //CALL API ADD NOTE
            if(description.isEmpty()&&title.isEmpty()&&(sImage==null || String.valueOf(sImage).isEmpty())){
                Log.e("save","Error");
                mProgressDialog.dismiss();
                Toast.makeText(NotesTakerActivity.this,"Please add some notes!",Toast.LENGTH_LONG).show();
            }else{
                ApiService.apiService.addNote(noteModel).enqueue(new Callback<NoteModel>() {
                    @Override
                    public void onResponse(Call<NoteModel> call, Response<NoteModel> response) {
                        mProgressDialog.dismiss();
                        Intent intent = new Intent();
                        String jsonString="";

                        GsonBuilder builder = new GsonBuilder();
                        builder.setPrettyPrinting();
                        Gson gson = builder.create();

                        jsonString = gson.toJson(response.body());
                        intent.putExtra("newNote",jsonString);
                        setResult(Activity.RESULT_OK,intent);
                        finish();
                    }

                    @Override
                    public void onFailure(Call<NoteModel> call, Throwable t) {
                        mProgressDialog.dismiss();
                        Log.e("picture",t.getMessage());
                        Toast.makeText(NotesTakerActivity.this,t.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
            }

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
            cardView.setVisibility(View.INVISIBLE);
            model_card.setTranslationY(2000);
            openGallery();
            return;
        }

        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
            cardView.setVisibility(View.INVISIBLE);
            model_card.setTranslationY(2000);
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