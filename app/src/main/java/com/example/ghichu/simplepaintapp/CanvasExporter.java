package com.example.ghichu.simplepaintapp;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

public class CanvasExporter
{
    private static final String DIRECTORY_PATH = "/Pictures/Paint";
    private static final String SAVE_FILE_NAME = "/drawing_";
    private static final String SHARE_FILE_NAME = "/shared_";
    private static final String FILE_EXTENSION = ".png";

    public static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 1;
    public static final int FLAG_SAVE = 1;
    public static final int FLAG_SHARE = 2;

    private final File subDirectory;

    private int exportType;

    public CanvasExporter()
    {
        // get the output storage directory and find the sub-directory.
        File storageDirectory = Environment.getExternalStorageDirectory();
        subDirectory = new File(storageDirectory.toString() + DIRECTORY_PATH);
    }

    public void setExportType (int exportType)
    {
        this.exportType = exportType;
    }

    public int getExportType ()
    {
        return exportType;
    }

    private boolean createDirectory ()
    {
        // if the sub-directory does not exist, create it
        if (!subDirectory.exists())
            return subDirectory.mkdir();
        // return true if it already exists
        return true;
    }

    public int getExistingFileCount(File directory)
    {
        int count = 0;
        // get the existing images as an array
        File[] existingImages = directory.listFiles();
        // if there is at least one image
        if (existingImages != null)
        {
            // loop through the existing images
            for (File file : existingImages)
            {
                // extract the file name and increment the counter if it is a valid file type
                String name = file.getName();
                if (name.endsWith(".jpg") || name.endsWith(".png"))
                    count++;
            }
        }
        return count;
    }

    private void outputToFileStream (File image, Bitmap bitmap)
    {
        FileOutputStream fileOutputStream;
        try
        {
            // compress the bitmap and link it to the output stream
            fileOutputStream = new FileOutputStream(image);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            // flush and close the output stream.
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e)
        {
            // throw an error message
            Log.w("ERROR", "" + e.getMessage());
        }
    }

    public String saveImage(Bitmap bitmap)
    {
        boolean created = createDirectory();
        // if the sub-directory exists or was created successfully
        if (subDirectory.exists() || created)
        {
            // create a new file for the bitmap
            int fileCount = getExistingFileCount(subDirectory);
            File image = new File(subDirectory, SAVE_FILE_NAME + ++fileCount + FILE_EXTENSION);
            outputToFileStream(image, bitmap);
            // return the path to the saved image.
            return image.getAbsolutePath();
        }
        return null;
    }

    public File getImage(Bitmap bitmap)
    {
        boolean created = createDirectory();
        // if the sub-directory exists or was created successfully
        if (subDirectory.exists() || created)
        {
            // create a new file for the bitmap to allow it to be shared
            File image = new File(subDirectory, SHARE_FILE_NAME + Math.random() + FILE_EXTENSION);
            outputToFileStream(image, bitmap);
            // return the image file
            return image;
        }
        return null;
    }
}