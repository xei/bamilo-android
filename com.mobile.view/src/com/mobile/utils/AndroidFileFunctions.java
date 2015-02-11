package com.mobile.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import android.content.Context;
import android.os.Environment;
import de.akquinet.android.androlog.Log;

public class AndroidFileFunctions {
    File myFile;
    public String fileName = "jumiatrack.txt";
    public AndroidFileFunctions() {
        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File (sdCard.getAbsolutePath() + "/jumia");
        dir.mkdirs();
        myFile = new File(dir,fileName);
        try {
            myFile.createNewFile();
        } catch (IOException e) {
            Log.i("REQUEST", "FAILED CREATING FILE!!!");
            e.printStackTrace();
        }
    }
  

    public void writeToFile(String value,
            Context context, int writeOrAppendMode) {
        try {
            FileOutputStream fOut = new FileOutputStream(myFile, true);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(value+"\n");
            myOutWriter.close();
            fOut.close();
        } 
        catch (Exception e) 
        {
            Log.i("REQUEST", "FAILED WRITING TO FILE!!!");
        }
    }
    

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
            Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}