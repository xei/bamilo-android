package com.bamilo.android.appmodule.bamiloapp.extlibraries.emarsys;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    private final ImageView bmImage;
    private int width = 0;
    private int height = 0;

    public DownloadImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    public DownloadImageTask(ImageView bmImage, int width, int height) {
        this.bmImage = bmImage;
        this.width = width;
        this.height = height;
    }

    protected Bitmap doInBackground(String... urls) {
        String url = urls[0];
        Bitmap icon = null;
        try {
            InputStream in = new java.net.URL(url).openStream();
            icon = BitmapFactory.decodeStream(in);
            if (width != 0 && height != 0) {
                icon = Bitmap.createScaledBitmap(icon, width, height, false);
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
        return icon;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
    }
}
