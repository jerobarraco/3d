package com.gedesoft.ddd.modelos;

import android.os.AsyncTask;

import com.gedesoft.ddd.vistas.MainActivity;

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
 * Created by Jonny on 08/06/2016.
 */
public class UsuariosAsync extends AsyncTask<String, Void, JSONObject> {

    public MainActivity mMainActivity;

    public UsuariosAsync(MainActivity activity){
        mMainActivity = activity;
    }


    @Override
    protected JSONObject doInBackground(String... params) {

        String Url = "http://test.grapot.co/login.php";

        try {

            String atk = params[0];
            String fid = params[1];
            URL url = new URL(Url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));

            String data = URLEncoder.encode("atk", "UTF-8")+"="+URLEncoder.encode(atk, "UTF-8")+"&"+
                          URLEncoder.encode("fid", "UTF-8")+"="+URLEncoder.encode(fid, "UTF-8");


            writer.write(data);
            writer.flush();
            writer.close();
            os.close();
            int responseCode = connection.getResponseCode();

            String response = "{ok:false, msg:'error en http'}";
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                response = "";
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            }

            JSONObject respuesta = new JSONObject(response);
            return respuesta;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject res) {
        super.onPostExecute(res);

        String mensaje;
        boolean ok = false;
        int uid = -1;
        String utk = "";
        if (res == null){
            mensaje ="Error raro";
        }else {
            try {
                if (!res.getBoolean("ok")){
                    mensaje = res.getString("msg");
                }else {
                    ok = true;
                    uid = res.getJSONObject("data").getInt("uid");
                    utk = res.getJSONObject("data").getString("utk");
                    mMainActivity.processFinish(utk, uid);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}
