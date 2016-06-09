package com.gedesoft.ddd.modelos;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.gedesoft.ddd.R;
import com.gedesoft.ddd.vistas.Agregar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Jonny on 29/05/2016.
 */
public class PostAsync extends AsyncTask<String, Void, JSONObject> {

    Context mContext;
    private ProgressDialog dialog;
    private ShareDialog shareDialog;
    public PostAsync(Context context, Agregar agregar){
        mContext = context;
        dialog = new ProgressDialog(agregar);
        shareDialog = new ShareDialog(agregar);
    }



    @Override
    protected JSONObject doInBackground(String... params) {

        String info_URL = "http://test.grapot.co/add.php";
        String method = params[0];
        if (method.equals("info")){
            String descr = params[1];
            String lat = params[2];
            String lon = params[3];
            String acc = params[4];
            String f64 = params[5];
            String cat = params[6];
            String uid = params[7];
            String utk = params[8];
            try {
                URL url = new URL(info_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                OutputStream os = connection.getOutputStream();
                BufferedWriter writer  = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                String data = URLEncoder.encode("descr", "UTF-8")+"="+URLEncoder.encode(descr, "UTF-8")+"&"+
                        URLEncoder.encode("lat", "UTF-8")+"="+URLEncoder.encode(lat, "UTF-8")+"&"+
                        URLEncoder.encode("lon", "UTF-8")+"="+URLEncoder.encode(lon, "UTF-8")+"&"+
                        URLEncoder.encode("acc", "UTF-8")+"="+URLEncoder.encode(acc, "UTF-8")+"&"+
                        URLEncoder.encode("cat", "UTF-8")+"="+URLEncoder.encode(cat, "UTF-8")+"&"+
                        URLEncoder.encode("uid", "UTF-8")+"="+URLEncoder.encode(uid, "UTF-8")+"&"+
                        URLEncoder.encode("utk", "UTF-8")+"="+URLEncoder.encode(utk, "UTF-8")+"&"+
                        URLEncoder.encode("f64", "UTF-8")+"="+URLEncoder.encode(f64, "UTF-8");

                writer.write(data);
                writer.flush();
                writer.close();
                os.close();
                int responseCode=connection.getResponseCode();


                String response = "{ok:false, msg:'error en http'}";
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    response = "";
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                }
                JSONObject washo = new JSONObject(response);
                return washo;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject res) {
        super.onPostExecute(res);
        if (dialog.isShowing()) {
            dialog.dismiss();
        }

        String mensaje = "";
        boolean ok = false;
        if(res== null){
            mensaje = "Error desconocido";

        }else{
            try {
                if(!res.getBoolean("ok")){
                    mensaje = res.getString("msg");
                }else{
                    ok = true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        if(ok){
            final AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                    .setTitle(mContext.getString(R.string.exito_postasync))
                    .setMessage(mContext.getString(R.string.exito_compartir_face_postasync))
                    .setCancelable(false)
                    .setNegativeButton(mContext.getString(R.string.no), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    ((Activity) mContext).finish();
                                }
                            }

                    )
                    .setPositiveButton(mContext.getString(R.string.si), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            ShareLinkContent content = new ShareLinkContent.Builder()
                                    .setContentUrl(Uri.parse("https://developers.facebook.com"))
                                    .setContentDescription(mContext.getString(R.string.mensaje_descrip_facebook_link))
                                    .setContentTitle(mContext.getString(R.string.title_facebook_link))
                                    .setQuote(mContext.getString(R.string.quote_facebook_link))
                                    .build();
                            shareDialog.show(content);

                            ((Activity) mContext).finish();
                        }
                    });
            builder.show();
        }else {
            final AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                    .setTitle(mContext.getString(R.string.error_operacion_postasync))
                    .setMessage(  mContext.getString(R.string.verificagpsinternet_postasync)+"\n"+mensaje)
                    .setCancelable(false)
                    .setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.show();
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.setCancelable(false);
        dialog.setMessage(mContext.getString(R.string.generando_marcador_postasync));
        dialog.show();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }


}
