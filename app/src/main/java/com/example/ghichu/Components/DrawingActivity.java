package com.example.ghichu.Components;

import static com.example.ghichu.Components.Display.beginPoint;
import static com.example.ghichu.Components.Display.colorList;
import static com.example.ghichu.Components.Display.current_brush;
import static com.example.ghichu.Components.Display.endPoint;
import static com.example.ghichu.Components.Display.pathList;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.view.View;

import com.example.ghichu.MainActivity;
import com.example.ghichu.R;

import java.util.ArrayList;

public class DrawingActivity extends AppCompatActivity {
    public static Path path = new Path();
    public static Paint paint_brush=new Paint();
    public static ArrayList<Integer> recordBeginPoint=new ArrayList<>();
    public static ArrayList<Integer> recordEndPoint=new ArrayList<>();
    public static ArrayList<Path> recordPathListOld=new ArrayList<>();
    public static ArrayList<Integer> recordColorListOld=new ArrayList<>();
    private Canvas canvas;

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

    public void goPrev(View view){
        if(pathList.size()>0){
            recordPathListOld.addAll(pathList.subList(beginPoint.get(beginPoint.size()-1),endPoint.get(endPoint.size()-1)));
            recordColorListOld.addAll(colorList.subList(beginPoint.get(beginPoint.size()-1),endPoint.get(endPoint.size()-1)));

            pathList.subList(beginPoint.get(beginPoint.size()-1),endPoint.get(endPoint.size()-1)).clear();
            colorList.subList(beginPoint.get(beginPoint.size()-1),endPoint.get(endPoint.size()-1)).clear();

            recordBeginPoint.add(beginPoint.get(beginPoint.size()-1));
            recordEndPoint.add(endPoint.get(endPoint.size()-1));

            beginPoint.remove(beginPoint.get(beginPoint.size()-1));
            endPoint.remove(endPoint.get(endPoint.size()-1));
            path.reset();
        }
    }

    public void goNext(View view){
        System.out.println("next");
        if(!recordPathListOld.isEmpty()&&recordPathListOld.size()>0){
            pathList.addAll(recordPathListOld.subList(recordBeginPoint.get(recordBeginPoint.size()-1),recordEndPoint.get(recordEndPoint.size()-1)));
            colorList.addAll(recordColorListOld.subList(recordBeginPoint.get(recordBeginPoint.size()-1),recordEndPoint.get(recordEndPoint.size()-1)));

            recordPathListOld.subList(recordBeginPoint.get(recordBeginPoint.size()-1),recordEndPoint.get(recordEndPoint.size()-1)).clear();
            recordColorListOld.subList(recordBeginPoint.get(recordBeginPoint.size()-1),recordEndPoint.get(recordEndPoint.size()-1)).clear();

            beginPoint.add(recordBeginPoint.get(recordBeginPoint.size()-1));
            endPoint.add(recordEndPoint.get(recordEndPoint.size()-1));

            recordBeginPoint.remove(recordBeginPoint.get(recordBeginPoint.size()-1));
            recordEndPoint.remove(recordEndPoint.get(recordEndPoint.size()-1));

            path.reset();
        }
    }
}