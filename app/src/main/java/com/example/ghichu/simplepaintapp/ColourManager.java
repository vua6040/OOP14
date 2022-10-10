package com.example.ghichu.simplepaintapp;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;

import com.example.ghichu.R;

/**
 * Class which holds various colour IDs and any static methods which interact with said colours.
 */
public class ColourManager
{
    private static final int GREY = R.color.colorGrey;
    private static final int PINK = R.color.colorPink;
    private static final int GREEN = R.color.colorGreen;
    private static final int BLUE_GREEN = R.color.colorBlueyGreen;
    private static final int PURPLE = R.color.colorPurple;
    private static final int BLUE = R.color.colorBlue;
    private static final int GREEN_BLUE = R.color.colorGreenyBlue;
    private static final int ORANGE = R.color.colorOrange;
    private static final int PINK_PURPLE = R.color.colorPinkyPurple;
    private static final int RED = R.color.colorRed;
    private static final int YELLOW = R.color.colorYellow;
    private static final int BLUE_PURPLE = R.color.colorBlueyPurple;
    private static final int BLACK = R.color.colorBlack;
    private static final int MINT_BLUE = R.color.colorMintyBlue;
    private static final int WHITE = R.color.colorWhite;

    private static final int RIM = R.color.colorRim;

    public static int getDefaultColour (Context context)
    {
        return ContextCompat.getColor(context, RED);
    }

    public static int getPenSizeRimColour(Context context)
    {
        return ContextCompat.getColor(context, RIM);
    }

    public static int[] getColourIDs ()
    {
        return new int[]
                {
                        GREY, PINK, GREEN, BLUE_GREEN, PURPLE,
                        BLUE, GREEN_BLUE, ORANGE, PINK_PURPLE, RED,
                        YELLOW, BLUE_PURPLE, BLACK, MINT_BLUE, WHITE
                };
    }

    public static boolean isSignificantlyLight (int colour)
    {
        return ColorUtils.calculateLuminance(colour) > 0.1;
    }

    public static boolean isSignificantlyDark (int colour)
    {
        return ColorUtils.calculateLuminance(colour) < 0.8;
    }
}