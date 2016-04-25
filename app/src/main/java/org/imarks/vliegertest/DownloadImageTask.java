package org.imarks.vliegertest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by Mark on 25-4-2016.
 */
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;

    public DownloadImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    @Override
    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon = null;
        try {
            InputStream inputStream = new URL(urldisplay).openStream();
            mIcon = BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            Log.e("Image Download", e.getMessage());
            e.printStackTrace();
        }
        return mIcon;
    }

    protected void onPostExecute(Bitmap result) {
        ViewGroup.LayoutParams params = bmImage.getLayoutParams();
        params.height = 360;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        bmImage.setImageBitmap(Bitmap.createScaledBitmap(result, 360, 360, false));
        bmImage.setLayoutParams(params);
        bmImage.requestLayout();
    }
}