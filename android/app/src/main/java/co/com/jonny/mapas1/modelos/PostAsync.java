package co.com.jonny.mapas1.modelos;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;

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

import co.com.jonny.mapas1.vistas.Agregar;

/**
 * Created by Jonny on 19/05/2016.
 */
public class PostAsync extends AsyncTask<String, Void, String> {

    Context mContext;
    private ProgressDialog dialog;
    public PostAsync(Context context, Agregar agregar){
        mContext = context;
        dialog = new ProgressDialog(agregar);
    }





    @Override
    protected String doInBackground(String... params) {

        String info_URL = "http://test.grapot.co/add.php";
        String method = params[0];
        if (method.equals("info")){
            String descr = params[1];
            String lat = params[2];
            String lon = params[3];
            String acc = params[4];
            String f64 = params[5];
            String cat = params[6];

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
                              URLEncoder.encode("f64", "UTF-8")+"="+URLEncoder.encode(f64, "UTF-8");

                writer.write(data);
                writer.flush();
                writer.close();
                os.close();
                int responseCode=connection.getResponseCode();


                String response = "{ok:false}";
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    response = "";
                    String line;
                    BufferedReader br=new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    while ((line=br.readLine()) != null) {
                        response += line;
                    }
                }
                JSONObject washo = new JSONObject(response);
                if (washo.getBoolean("ok")){
                    return "Envío exitoso";
                }else{
                    return "Error "+washo.getString("msg");
                }
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
    protected void onPostExecute(String res) {
        super.onPostExecute(res);
        if (dialog.isShowing()) {
            dialog.dismiss();
        }

        if(res != null){
            final AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                    .setTitle("Operación exitosa")
                    .setMessage("Has añadido el marcador con éxito. Gracias por tu información.")
                    .setCancelable(true)
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            ((Activity) mContext).finish();
                        }
                    });
            builder.show();
        }else {
            final AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                    .setTitle("Error en la operación")
                    .setMessage("Verifica tu conexión a internet y asegúrate que tu GPS esté encendido e intenta de nuevo")
                    .setCancelable(true)
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
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
        dialog.setMessage("Generando marcador, por favor espera");
        dialog.show();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }


}
