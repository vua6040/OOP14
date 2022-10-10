package com.example.ghichu.simplepaintapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.ghichu.R;

import java.util.ArrayList;

public class ColourPickerDialog extends Dialog implements View.OnClickListener
{
    private ColourPickerOptionSelectedListener listener;

    private final ArrayList<ColourButton> colourButtons;

    public ColourPickerDialog(@NonNull Context context, int currentColour)
    {
        // set the current activity using the context
        super(context);
        setOwnerActivity ((Activity) context);
        // define the window and set the background as transparent to allow for rounded corners
        Window window = super.getWindow ();
        if (window != null)
            window.setBackgroundDrawable (new ColorDrawable(Color.TRANSPARENT));
        // set the resource to be used for the dialog
        super.setContentView (R.layout.activity_colour_picker_dialog);
        super.setCancelable (true);
        // Assign variables to the three rows of colour buttons
        LinearLayout colourColumn1 = findViewById(R.id.columnLayout1);
        LinearLayout colourColumn2 = findViewById(R.id.columnLayout2);
        LinearLayout colourColumn3 = findViewById(R.id.columnLayout3);
        // Create an ArrayList of the colour buttons
        ArrayList<Integer> colours = new ArrayList<>();
        addColours(colours, colourColumn1);
        addColours(colours, colourColumn2);
        addColours(colours, colourColumn3);
        // Extract the various colour IDs to be assigned
        final int[] colourIDs = ColourManager.getColourIDs();
        // Create an ArrayList for the colour buttons
        colourButtons = new ArrayList<>();
        // Loop through the colour buttons extracted from the layout
        for (int i = 0; i < colours.size(); i++)
        {
            // Create a view object for the colour button and set a click listener
            View view = findViewById(colours.get(i));
            view.setOnClickListener(this);
            // Get the colour representation of the colour ID
            int colour = ContextCompat.getColor(context, colourIDs[i]);
            // Adjust the colour of the button to match
            GradientDrawable gradientDrawable = (GradientDrawable) view.getBackground();
            gradientDrawable.setColor(ContextCompat.getColor(context, colourIDs[i]));
            // Get the resource ID for the tick icon
            int tickResourceID = getTickResourceID(colour);
            // Create a ColourButton object using the View ID, colour ID, and "tick" ID
            ColourButton colourButton = new ColourButton(colours.get(i), colourIDs[i], tickResourceID);
            colourButtons.add(colourButton);
            // Set the "tick" icon if the current colour is selected in CanvasView
            if (colour == currentColour)
                ((ImageButton) view).setImageResource(tickResourceID);
        }
        // Initialise the buttons
        Button buttonCancel = findViewById(R.id.buttonCancel);
        Button buttonSelect = findViewById(R.id.buttonSelect);
        // Set the click listeners for the buttons
        buttonCancel.setOnClickListener(this);
        buttonSelect.setOnClickListener(this);
    }

    public void setOnDialogOptionSelectedListener (ColourPickerOptionSelectedListener listener)
    {
        this.listener = listener;
    }

    private void addColours (ArrayList<Integer> colours, LinearLayout colourColumn)
    {
        // loop through the LinearLayout which holds all the colours and add them to the array
        for (int i = 0; i < colourColumn.getChildCount(); i++)
            colours.add(colourColumn.getChildAt(i).getId());
    }

    private boolean isNightMode ()
    {
        return (getContext().getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
    }

    private int getTickResourceID (int colour)
    {
        int tickResourceID;
        if (isNightMode())
        {
            // prefer white "tick" drawables over black
            tickResourceID = R.drawable.ic_done_black_24dp;
            if (ColourManager.isSignificantlyDark(colour))
                tickResourceID = R.drawable.ic_done_white_24dp;
        } else
        {
            // prefer black "tick" drawables over white
            tickResourceID = R.drawable.ic_done_white_24dp;
            if (ColourManager.isSignificantlyLight(colour))
                tickResourceID = R.drawable.ic_done_black_24dp;
        }
        return tickResourceID;
    }

    @Override
    public void onClick(View v)
    {
        // get the ID of the view object being clicked
        int viewID = v.getId();
        // prompt the listener depending on the option selected
        if (viewID == R.id.buttonCancel || viewID == R.id.buttonSelect)
        {
            dismiss();
        } else
        {
            // loop through the colour button drawables
            for (ColourButton colourButton : colourButtons)
            {
                // if the clicked view object is the same as the colour button
                if (colourButton.getViewID() == viewID)
                {
                    // get the colour of the button
                    int colour = ContextCompat.getColor(getContext(), colourButton.getColourID());
                    // set the "tick" icon overlay to show the colour is selected
                    ((ImageButton) v).setImageResource(colourButton.getSelectedIconID());
                    listener.onColourPickerOptionSelected(colour);
                } else
                {
                    // get the button as a view and remove any pre-existing "tick" drawable
                    View view = findViewById(colourButton.getViewID());
                    ((ImageButton) view).setImageDrawable(null);
                }
            }
        }
    }

    private static class ColourButton
    {
        private final int viewID;
        private final int colourID;
        private final int selectedIconID;

        public ColourButton(int viewID, int colourID, int selectedIconID)
        {
            this.viewID = viewID;
            this.colourID = colourID;
            this.selectedIconID = selectedIconID;
        }

        public int getViewID()
        {
            return viewID;
        }

        public int getColourID()
        {
            return colourID;
        }

        public int getSelectedIconID ()
        {
            return selectedIconID;
        }
    }

    public interface ColourPickerOptionSelectedListener
    {
        void onColourPickerOptionSelected (int colour);
    }
}