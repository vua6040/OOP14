package com.example.ghichu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.VideoCapture;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.ghichu.components.NotesTakerActivity;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class CameraPicture extends AppCompatActivity implements View.OnClickListener, ImageAnalysis.Analyzer {

    ImageButton imageView_back, imageView_save, imageView_capture;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    PreviewView previewView;
    private ImageCapture imageCapture;
    private VideoCapture videoCapture;
    private ImageAnalysis imageAlalysis;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_picture);

        imageView_back = findViewById(R.id.imageView_back);

        imageView_save = findViewById(R.id.imageView_save);
        imageView_save.setOnClickListener(this);

        imageView_capture = findViewById(R.id.imageView_capture);
        imageView_capture.setOnClickListener(this);

        previewView = findViewById(R.id.previewView);

        imageView_back.setOnClickListener(view -> {
            Intent i = new Intent(CameraPicture.this, NotesTakerActivity.class);
            startActivity(i);
        });

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                startCameraX(cameraProvider);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }, getExecutor());

    }

    //CAMERA
    private Executor getExecutor() {
        return ContextCompat.getMainExecutor(this);
    }

    @SuppressLint("RestrictedApi")
    private void startCameraX(ProcessCameraProvider cameraProvider) {
        cameraProvider.unbindAll();

        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        //image capture
        imageCapture = new ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build();

        //video capture use case
        videoCapture = new VideoCapture.Builder().setVideoFrameRate(30).build();

        imageAlalysis = new ImageAnalysis.Builder().setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build();

        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture,imageAlalysis);

    }

    @SuppressLint("RestrictedApi")
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageView_capture:
                if(imageView_capture.isClickable()==false){
                    imageView_capture.setClickable(true);
                    recordVideo();
                }else{
                    videoCapture.stopRecording();
                }
                break;
            case R.id.imageView_save:
                capturePhoto();
                break;
        }
    }

    @SuppressLint("RestrictedApi")
    private void recordVideo() {
        if (videoCapture != null) {

            File moveDir = new File("/mnt/sdcard/Movies/CameraMovies");
            if (!moveDir.exists()) {
                moveDir.mkdir();
            }

            Date date = new Date();
            String timestamp = String.valueOf(date.getTime());
            String photoFilePath = moveDir.getAbsolutePath() + "/" + timestamp + ".mp4";

            File vidFile = new File(photoFilePath);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            videoCapture.startRecording(
                    new VideoCapture.OutputFileOptions.Builder(vidFile).build(),
                    getExecutor(),
                    new VideoCapture.OnVideoSavedCallback() {
                        @Override
                        public void onVideoSaved(@NonNull VideoCapture.OutputFileResults outputFileResults) {
                            Toast.makeText(CameraPicture.this,"Video has been saved successful",Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onError(int videoCaptureError, @NonNull String message, @Nullable Throwable cause) {
                            Toast.makeText(CameraPicture.this,"Error saving the video: "+message,Toast.LENGTH_LONG).show();
                        }
                    }
            );
        }
    }

    private void capturePhoto() {
        System.out.println("clicked");
        File photoDir = new File("C:\\Users\\ADMIN\\Documents\\PTIT\\Ghichu\\app\\src\\main\\res\\img");
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
    }

    @Override
    public void analyze(@NonNull ImageProxy image) {
        Log.d("mainactivity analyze","analyze: got the frame at: "+image.getImageInfo().getTimestamp());
        image.close();
    }
}