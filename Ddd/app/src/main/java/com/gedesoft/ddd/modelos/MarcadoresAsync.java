package com.gedesoft.ddd.modelos;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;

import com.gedesoft.ddd.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by Jonny on 31/05/2016.
 */
public class MarcadoresAsync extends AsyncTask<String, String, List<MarcadoresDatos>>{

    private boolean mapReady;
    private GoogleMap mMap;
    private Activity mActivity;
    private Hashtable<String, MarcadoresDatos> mapa;
    public MarcadoresAsync(boolean mapReady, GoogleMap map, Activity context,Hashtable<String, MarcadoresDatos> mapa){
        this.mapReady = mapReady;
        mMap = map;
        this.mActivity = context;
        this.mapa = mapa;
    }

    @Override
    protected List<MarcadoresDatos> doInBackground(String... params) {

        if (!mapReady) {
        } else {
            HttpURLConnection connection = null;
            BufferedReader bufferedReader = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer Buffer = new StringBuffer();

                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    Buffer.append(line);
                }

                String completeJson = Buffer.toString();
                JSONObject fullJsonCall = new JSONObject(completeJson);
                JSONArray arrayMarcadores = fullJsonCall.getJSONArray("data");


                //List<MarkerOptions> marcadoreslista = new ArrayList<MarkerOptions>();
                ArrayList<MarcadoresDatos> marcadoresDatoses = new ArrayList<>();
                for (int i = 0; i < arrayMarcadores.length(); i++) {
                    MarcadoresDatos marcadoresDatos = new MarcadoresDatos();
                    JSONObject marcadorFinal = arrayMarcadores.getJSONObject(i);
                    marcadoresDatos.setLatitud(marcadorFinal.getDouble("lat"));
                    marcadoresDatos.setIdMarker(marcadorFinal.getInt("idn"));
                    marcadoresDatos.setLongitud(marcadorFinal.getDouble("lon"));
                    marcadoresDatos.setTexto(marcadorFinal.getString("descr"));
                    marcadoresDatos.setCategorias(marcadorFinal.getInt("cat"));

                    marcadoresDatoses.add(marcadoresDatos);


                }

                return marcadoresDatoses;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }

                try {
                    if (bufferedReader != null) {
                        bufferedReader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }
        return null;
    }

    @Override
    protected void onPostExecute(List<MarcadoresDatos> resultado) {
        super.onPostExecute(resultado);

        mMap.clear();
        mapa.clear();


        if (resultado == null) {
            Snackbar.make(mActivity.findViewById(android.R.id.content), "Verifica tu conexión a internet", Snackbar.LENGTH_LONG).show();
        } else {


            for (int i = 0; i < resultado.size(); i++) {
                MarcadoresDatos currentMarker = resultado.get(i);
                Marker marcador = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(currentMarker.getLatitud(), currentMarker.getLongitud()))
                        .title(currentMarker.getTexto())
                        .icon(BitmapDescriptorFactory.fromBitmap(Marcadores(currentMarker.getCategorias())))
                );

                mapa.put(marcador.getId(), currentMarker);
//                   String url = "http://test.grapot.co/s/i/"+String.valueOf(currentMarker.getIdMarker())+".jpg";
//                   new DownloadImageTask(marcador.getId(), mapa).execute(url);

            }
        }

    }

    public Bitmap Marcadores(int marcadoresDatos) {
        int height = 100;
        int width = 100;

        //Animales
        BitmapDrawable bitmapdraw = (BitmapDrawable) mActivity.getResources().getDrawable(R.drawable.dog);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap perroMarker = Bitmap.createScaledBitmap(b, width, height, false);

        //Ambientales
        BitmapDrawable bitmapambientales = (BitmapDrawable) mActivity.getResources().getDrawable(R.drawable.ambientales);
        Bitmap b2 = bitmapambientales.getBitmap();
        Bitmap ambientalMarker = Bitmap.createScaledBitmap(b2, width, height, false);

        //Policiales
        BitmapDrawable bitmapPoliciales = (BitmapDrawable) mActivity.getResources().getDrawable(R.drawable.policiales);
        Bitmap b3 = bitmapPoliciales.getBitmap();
        Bitmap policialesMarker = Bitmap.createScaledBitmap(b3, width, height, false);

        //Legales
        BitmapDrawable bitmapLegales = (BitmapDrawable) mActivity.getResources().getDrawable(R.drawable.legales);
        Bitmap b4 = bitmapLegales.getBitmap();
        Bitmap legalesMarker = Bitmap.createScaledBitmap(b4, width, height, false);

        //Servicios
        BitmapDrawable bitmapServicios = (BitmapDrawable) mActivity.getResources().getDrawable(R.drawable.servicios);
        Bitmap b5 = bitmapServicios.getBitmap();
        Bitmap serviciosMarker = Bitmap.createScaledBitmap(b5, width, height, false);

        //Sociales
        BitmapDrawable bitmapSociales = (BitmapDrawable) mActivity.getResources().getDrawable(R.drawable.sociales);
        Bitmap b6 = bitmapSociales.getBitmap();
        Bitmap socialesMarker = Bitmap.createScaledBitmap(b6, width, height, false);

        //Negocios
        BitmapDrawable bitmapNegocios = (BitmapDrawable) mActivity.getResources().getDrawable(R.drawable.negocios);
        Bitmap b7 = bitmapNegocios.getBitmap();
        Bitmap negociosMarker = Bitmap.createScaledBitmap(b7, width, height, false);

        //Emergencias
        BitmapDrawable bitmapEmergencias = (BitmapDrawable) mActivity.getResources().getDrawable(R.drawable.emergencias);
        Bitmap b8 = bitmapEmergencias.getBitmap();
        Bitmap emergenciasMaker = Bitmap.createScaledBitmap(b8, width, height, false);

        //Default
        BitmapDrawable bitmapDefault = (BitmapDrawable) mActivity.getResources().getDrawable(R.drawable.defaultmarker);
        Bitmap b0 = bitmapDefault.getBitmap();
        Bitmap DefaultMaker = Bitmap.createScaledBitmap(b0, width, height, false);


        switch (marcadoresDatos) {
            case 0:
                return DefaultMaker;
            case 1:
                return perroMarker;
            case 2:
                return ambientalMarker;
            case 3:
                return policialesMarker;
            case 4:
                return legalesMarker;
            case 5:
                return serviciosMarker;
            case 6:
                return socialesMarker;
            case 7:
                return negociosMarker;
            case 8:
                return emergenciasMaker;
            default:
                return DefaultMaker;

        }
    }

}


