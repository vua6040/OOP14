package com.example.ghichu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import java.util.Timer;
import java.util.TimerTask;

public class IntroductionActivity extends AppCompatActivity {

    TextView textView;
    Timer timer;
    ImageView introImg;
    LottieAnimationView lottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);

        introImg = findViewById(R.id.bgIntro);
        lottieAnimationView = findViewById(R.id.lottie_intro);
        textView = findViewById(R.id.textView2);

        introImg.animate().translationY(-2900).setDuration(1600).setStartDelay(2100);
        lottieAnimationView.animate().translationY(2000).setDuration(1600).setStartDelay(2100);
        textView.animate().translationX(2000).setDuration(1600).setStartDelay(2200);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent introActive = new Intent(IntroductionActivity.this, MainActivity.class);
                startActivity(introActive);
            }
        }, 5000);
    }

}