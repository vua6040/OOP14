package com.example.ghichu.simplepaintapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class CanvasView extends View
{
    private static final int DEFAULT_BG_COLOUR = Color.TRANSPARENT;
    private static final int DEFAULT_STROKE_WIDTH = 15;
    private static final int TOUCH_TOLERANCE = 4;

    private final ArrayList<DrawPath> undo;
    private final ArrayList<DrawPath> redo;

    private Canvas canvas;
    private Bitmap bitmap;
    private final Paint paint;
    private Path path;

    private int currentColour;
    private int backgroundColour;
    private int previousStrokeWidth;
    private int strokeWidth;

    private float x, y;

    private boolean invalidTouch;

    public CanvasView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        // define and add set attributes for the paint object
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setXfermode(null);
        paint.setAlpha(0xff);
        // initialise the undo and redo ArrayList objects
        undo = new ArrayList<>();
        redo = new ArrayList<>();
    }

    public void initialise (int width, int height)
    {
        // set up the colours, widths, etc
        currentColour = ColourManager.getDefaultColour(getContext());
        backgroundColour = DEFAULT_BG_COLOUR;
        previousStrokeWidth = DEFAULT_STROKE_WIDTH;
        strokeWidth = DEFAULT_STROKE_WIDTH;
        // set the colour for the paint object
        paint.setColor(currentColour);
        // create a bitmap and canvas object to allow for saving as an image
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
    }

    public Bitmap getBitmap()
    {
        return bitmap;
    }

    public void setColour(int colour)
    {
        currentColour = colour;
    }

    public int getColour ()
    {
        return currentColour;
    }

    public void setPreviousStrokeWidth(int strokeWidth)
    {
        this.previousStrokeWidth = strokeWidth;
    }

    public int getPreviousStrokeWidth()
    {
        return previousStrokeWidth;
    }

    public void setStrokeWidth(int strokeWidth)
    {
        this.strokeWidth = strokeWidth;
    }

    public int getStrokeWidth()
    {
        return strokeWidth;
    }

    public void undo ()
    {
        // if the user has performed an action
        if (undo.size() > 0)
        {
            // add the drawn object to the redo list and re-draw
            redo.add(undo.remove(undo.size() - 1));
            drawPaths();
        }
    }

    public void redo ()
    {
        // if the user has performed an action
        if (redo.size() > 0)
        {
            // add the drawn object to the undo list and re-draw
            undo.add(redo.remove(redo.size() - 1));
            drawPaths();
        }
    }

    public void clear()
    {
        // reset the background color and set the background to be a clear
        backgroundColour = DEFAULT_BG_COLOUR;
        this.canvas.drawColor(backgroundColour, PorterDuff.Mode.CLEAR);
        // empty the lists and redraw the canvas
        undo.clear();
        redo.clear();
        invalidate();
    }

    public void handleTouches (float x, float y, int action)
    {
        switch (action)
        {
            // determine which action is being performed and redraw the canvas
            case MotionEvent.ACTION_DOWN:
                // the user is pressing down on the canvas
                touchStart(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                // the user is moving while pressing down on the canvas
                touchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                // the user has lifted up after pressing down on the canvas
                touchUp();
                invalidate();
                break;
        }
    }

    private void touchStart (float x, float y)
    {
        if(Math.abs(this.getHeight() - y) < 50 || y < 30)
        {
            // ensure the user isn't touching near the status or navigation bar
            invalidTouch = true;
        } else
        {
            // create a new Path object
            path = new Path();
            // create a new DrawPath object
            DrawPath drawPath = new DrawPath(currentColour, strokeWidth, path);
            undo.add(drawPath);
            // reset the path and move it to the coordinates
            path.reset();
            path.moveTo(x, y);
            // update x and y global variables
            this.x = x;
            this.y = y;
        }
    }

    private void touchMove(float x, float y)
    {
        // if the touch is not invalid
        if (!invalidTouch)
        {
            // calculate the difference in x and y
            float dx = Math.abs(x - this.x);
            float dy = Math.abs(y - this.y);
            // if the difference in x or y is greater than the minimum tolerance
            if ((dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE))
            {
                // move the path to the touched coordinates
                path.quadTo(this.x, this.y, (x + this.x) / 2, (y + this.y) / 2);
                // update x and y global variables
                this.x = x;
                this.y = y;
            }
        }
    }

    private void touchUp()
    {
        // if the touch is not invalid, draw a line to the point
        if (!invalidTouch)
            path.lineTo(this.x, this.y);
        // reset the invalid pointer
        invalidTouch = false;
    }

    private void drawPaths ()
    {
        // draw the clear background
        this.canvas.drawColor(backgroundColour, PorterDuff.Mode.CLEAR);
        // loop through each DrawPath object
        for (DrawPath drawPath : undo)
        {
            // set the paint object attributes
            paint.setColor(drawPath.getColour());
            paint.setStrokeWidth(drawPath.getWidth());
            paint.setMaskFilter(null);
            // draw the path
            this.canvas.drawPath(drawPath.getPath(), paint);
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        canvas.save();
        // if there is more than one previous path
        if (undo.size() != 0)
        {
            // get the most recently drawn path
            DrawPath lastPath = undo.get(undo.size() - 1);
            // set the paint object attributes
            paint.setColor(lastPath.getColour());
            paint.setStrokeWidth(lastPath.getWidth());
            paint.setMaskFilter(null);
            // draw the path
            this.canvas.drawPath(lastPath.getPath(), paint);
        }
        // draw the bitmap to the canvas
        canvas.drawBitmap(bitmap, 0, 0, paint);
        canvas.restore();
    }
}
