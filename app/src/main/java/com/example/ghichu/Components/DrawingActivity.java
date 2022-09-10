package com.example.ghichu.Components;

import static com.example.ghichu.Components.Display.colorList;
import static com.example.ghichu.Components.Display.current_brush;
import static com.example.ghichu.Components.Display.pathList;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.view.View;

import com.example.ghichu.MainActivity;
import com.example.ghichu.R;

public class DrawingActivity extends AppCompatActivity {
    public static Path path = new Path();
    public static Paint paint_brush=new Paint();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);
    }

    public void pencil(View view){
        paint_brush.setColor(Color.BLACK);
        currentColor(paint_brush.getColor());
    }

    public void eraser(View view){
        pathList.clear();
        colorList.clear();
        path.reset();
    }

    public void redColor(View view){
        paint_brush.setColor(Color.RED);
        currentColor(paint_brush.getColor());
    }

    public void yellowColor(View view){
        paint_brush.setColor(Color.YELLOW);
        currentColor(paint_brush.getColor());
    }

    public void greenColor(View view){
        paint_brush.setColor(Color.GREEN);
        currentColor(paint_brush.getColor());
    }

    public void purpleColor(View view){
        paint_brush.setColor(Color.MAGENTA);
        currentColor(paint_brush.getColor());
    }

    public void blueColor(View view){
        paint_brush.setColor(Color.BLUE);
        currentColor(paint_brush.getColor());
    }
    public void currentColor(int c){
        current_brush=c;
        path=new Path();
    }

    public void goBack(View view){
        Intent i = new Intent(DrawingActivity.this, MainActivity.class);
        startActivity(i);
    }
}