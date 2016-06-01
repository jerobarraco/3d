package com.gedesoft.ddd.modelos;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.util.Hashtable;

/**
 * Created by Jonny on 31/05/2016.
 */
    public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    private String mid;
    Hashtable<String, MarcadoresDatos> mapa;

    public DownloadImageTask(String pmid, Hashtable<String, MarcadoresDatos> mapas) {
        mid = pmid;
        this.mapa = mapas;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {

        if (result != null) {
            MarcadoresDatos m = mapa.get(mid);
            if (m != null) {
                m.setImg(result);
            } else {
                Log.w("TAG", "wtf me hiciste cargar una imagen para un marker que ya no existe?");
            }

        }
    }
}
