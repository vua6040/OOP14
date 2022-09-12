package com.example.ghichu.Components;

import static com.example.ghichu.Components.DrawingActivity.paint_brush;
import static com.example.ghichu.Components.DrawingActivity.path;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import java.util.ArrayList;
//Canvas
public class Display extends View {
    public static ArrayList<Path> pathList=new ArrayList<>();
    public static ArrayList<Integer> colorList=new ArrayList<>();
    public static ArrayList<Integer> beginPoint=new ArrayList<>();
    public static ArrayList<Integer> endPoint=new ArrayList<>();


    public ViewGroup.LayoutParams params;
    public static int current_brush = Color.BLACK;


    public Display(Context context) {
        super(context);
        init(context);
    }

    public Display(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Display(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        paint_brush.setAntiAlias(true);
        paint_brush.setColor(Color.BLACK);
        paint_brush.setStyle(Paint.Style.STROKE);
        paint_brush.setStrokeCap(Paint.Cap.ROUND);
        paint_brush.setStrokeJoin(Paint.Join.ROUND);
        paint_brush.setStrokeWidth(10f);
        params=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x= e.getX();
        float y =e.getY();
        switch(e.getAction()){
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x,y);
                beginPoint.add(pathList.size());
                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(x,y);
                pathList.add(path);
                colorList.add(current_brush);
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                endPoint.add(pathList.size());
                invalidate();
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for(int i=0;i<pathList.size();i++){
            paint_brush.setColor(colorList.get(i));
            canvas.drawPath(pathList.get(i),paint_brush);
            invalidate();
        }
        System.out.println("ok");
    }

}
