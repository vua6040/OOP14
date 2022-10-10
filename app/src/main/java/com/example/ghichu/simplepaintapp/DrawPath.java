package com.example.ghichu.simplepaintapp;

import android.graphics.Path;

public class DrawPath
{
    private final int colour;
    private final int width;

    private final Path path;

    public DrawPath(int colour, int width, Path path)
    {
        this.colour = colour;
        this.width = width;
        this.path = path;
    }

    public int getColour() {
        return colour;
    }

    public int getWidth() {
        return width;
    }

    public Path getPath() {
        return path;
    }
}
