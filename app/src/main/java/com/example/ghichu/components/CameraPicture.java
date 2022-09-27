package com.example.ghichu.components;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.ghichu.R;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class CameraPicture extends AppCompatActivity {

    ImageButton imageView_back,imageView_save,imageView_capture;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    PreviewView previewView;
    private ImageCapture imageCapture;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_picture);

        imageView_save=findViewById(R.id.imageView_save);
        imageView_back=findViewById(R.id.imageView_back);
        imageView_capture=findViewById(R.id.imageView_capture);
        previewView = findViewById(R.id.previewView);

        imageView_back.setOnClickListener(view -> {
            Intent i = new Intent(CameraPicture.this,NotesTakerActivity.class);
            startActivity(i);
        });

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(()->{
            try{
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                startCameraX(cameraProvider);
            }catch (ExecutionException e){
                e.printStackTrace();
            }catch (InterruptedException e){
                e.printStackTrace();
            }

        },getExecutor());

        imageView_capture.setOnClickListener(view -> {
            File photoDir = new File("mnt//sdcard//Pictures//CameraXPhoto");
            if(!photoDir.exists()){
                photoDir.mkdir();
            }

            Date date = new Date();
            String timestamp = String.valueOf(date.getTime());
            String photoFilePath = photoDir.getAbsolutePath()+"/"+timestamp+".jpg";

            File photoFile=new File(photoFilePath);

            imageCapture.takePicture(
                    new ImageCapture.OutputFileOptions.Builder(photoFile).build(), getExecutor(), new ImageCapture.OnImageSavedCallback() {
                        @Override
                        public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                            Toast.makeText(CameraPicture.this,"Photo has been saved successful",Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onError(@NonNull ImageCaptureException exception) {
                            Toast.makeText(CameraPicture.this,"Error save photo: "+exception.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
            );
        });
    }

    //CAMERA
    private Executor getExecutor(){
        return ContextCompat.getMainExecutor(this);
    }

    private void startCameraX(ProcessCameraProvider cameraProvider) {
        cameraProvider.unbindAll();

        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        //image capture
        imageCapture = new ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build();

        cameraProvider.bindToLifecycle(this,cameraSelector ,preview,imageCapture);

    }
}