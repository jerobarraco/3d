package co.com.jonny.mapas1.vistas;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
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

import co.com.jonny.mapas1.R;
import co.com.jonny.mapas1.modelos.MarcadoresDatos;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, android.location.LocationListener {
    private static final int CODIGO_SOLICITUD_LOCATION = 1;
    private GoogleMap mMap;
    private boolean mapReady = false;
    private double lat;
    private double lng;
    private LocationManager mLocationManager;
    private Marker marcador;
    private double norte;
    private double este;
    private  double sur;
    private double oeste;
    private int acc;
    private FloatingActionButton mFloatingActionButton;
    private Marker lastOpenned = null;
    private ImageView imgInfoWindow;
    private Hashtable<String, MarcadoresDatos> mapa = new Hashtable<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.fab);

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Categorias.class);
                intent.putExtra("Latitud", lat);
                intent.putExtra("Longitud", lng);
                intent.putExtra("Acc", acc);
                startActivity(intent);
            }
        });



        setSupportActionBar(toolbar);
        CheckEnableGPS();


        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET
                }, CODIGO_SOLICITUD_LOCATION);
                return;
            }
        }else{
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 7000, 10, this); // TODO 1
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_Fragment);
        mapFragment.getMapAsync(this);

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    /**
     * Método que nos comunica si el mapa ya está listo y ejecuta las cosas solo cuando lo está.
     * @param googleMap
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapReady = true;
        mMap = googleMap;
        if  (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String [] {Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, CODIGO_SOLICITUD_LOCATION);
            return;
        }
        }else {
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                public boolean onMarkerClick(Marker marker) {
                    // Check if there is an open info window

                    if (lastOpenned != null) {
                        // Close the info window
                        lastOpenned.hideInfoWindow();

                        // Is the marker the same marker that was already open
                        if (lastOpenned.equals(marker)) {
                            // Nullify the lastOpenned object
                            lastOpenned = null;
                            // Return so that the info window isn't openned again
                            return true;
                        }
                    }

                    // Open the info window for the marker
                    marker.showInfoWindow();
                    // Re-assign the last openned such that we can close it later
                    lastOpenned = marker;

                    // Event was handled by our code do not launch default behaviour.
                    return true;
                }
            });

            mMap.setMyLocationEnabled(true);

            mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {
                    LatLngBounds curScreen = mMap.getProjection().getVisibleRegion().latLngBounds;

                    //JSON
                    norte = curScreen.northeast.latitude;
                    este = curScreen.northeast.longitude;
                    sur = curScreen.southwest.latitude;
                    oeste = curScreen.southwest.longitude;

                    new Marcadores().execute("http://test.grapot.co/q.php?n=" + norte + "&e=" + este + "&s=" + sur + "&w=" + oeste);
                }
            });

            LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
            boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (!enabled) {

            } else {
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Criteria criteria = new Criteria();//3

                Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
                if(location != null){
                    Log.e("TAG", "Ahora si funco");
                }



                // Setting a custom info window adapter for the google map
                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    private final Hashtable<String, Bitmap> markerSet = new Hashtable<>();
                    // Use default InfoWindow frame
                    @Override
                    public View getInfoWindow(Marker arg0) {
                        return null;
                    }

                    // Defines the contents of the InfoWindow
                    @Override
                    public View getInfoContents(Marker arg0) {
                        // Getting view from the layout file info_window_layout
                        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View v = inflater.inflate(R.layout.info_marker_window, null);

                        // Getting the position from the marker
                         imgInfoWindow = (ImageView) v.findViewById(R.id.tv_img);

                        if (imgInfoWindow == null){
                            Log.e("TAG", "error");
                        }

                        String mid = arg0.getId();

                        if (mapa.containsKey(mid)){
                            MarcadoresDatos md = mapa.get(mid);
                            imgInfoWindow.setImageBitmap(md.getImg());
                        }else{
                            Log.i("MIDERROR","el marker "+mid+" no tiene foto");
                        }

                         TextView tvLat = (TextView) v.findViewById(R.id.tv_lat);
                        tvLat.setText(arg0.getTitle());
                        // Getting reference to the TextView to set longitude
                        TextView tvLng = (TextView) v.findViewById(R.id.tv_lng);


                        return v;

                    }
                });


            }

        }

    }

    /**
     * Método para verificar qué pasa cuando se pidió el permiso en runtime para habilitar el GPS (Android M,6,23)
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case CODIGO_SOLICITUD_LOCATION:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Snackbar.make(findViewById(android.R.id.content), "Ya has autorizado los permisos", Snackbar.LENGTH_SHORT).show();
                }

        }
    }

    /**
     * Método para saber si el GPS está encendido o no y ayudar al usuario a encenderlo
     */
    private void CheckEnableGPS(){
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!enabled) {
            AlertDialog.Builder builder= new  AlertDialog.Builder(this)
                    .setTitle("GPS no activado")
                    .setMessage("¿Deseas activar tu GPS? No podrás usar la aplicación sin activar tu GPS de manera correcta")
                    .setCancelable(true)
                    .setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setPositiveButton("Activar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                            finish();
                        }
                    });
            builder.show();
        }

    }

        //MÉTODOS LOCATION LISTENER
    @Override
    public void onLocationChanged(Location location) {
        LatLngBounds curScreen = mMap.getProjection().getVisibleRegion().latLngBounds;
//        LatLngBounds curScreen = mMap.getProjection()
        //JSON
        norte = curScreen.northeast.latitude;
        este = curScreen.northeast.longitude;
        sur = curScreen.southwest.latitude;
        oeste = curScreen.southwest.longitude;
        double olat=lat, olng=lng;

        lat = location.getLatitude();
        lng = location.getLongitude();
        acc = (int) location.getAccuracy();

        if (olat ==0 || olng == 0){ //TODO 2
            LatLng inicial = new LatLng(lat, lng);
            CameraPosition target = CameraPosition.builder()
                    .target(inicial)
                    .zoom(15)
                    .bearing(360)
                    .tilt(40)
                        .build();

            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(target));
        }

//        mTextView.setText("Estás en " + curScreen);
        //Log.e("TAG", "Estás en " + lat + lng);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }




    //Aquí inicia JSON

    public class Marcadores extends AsyncTask<String, String, List<MarcadoresDatos>> {


        @Override
        protected List<MarcadoresDatos> doInBackground(String... params) {

            if (!mapReady){}else{
                HttpURLConnection connection = null;
                BufferedReader bufferedReader = null;
                try{
                    URL url = new URL(params[0]);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

                    InputStream stream = connection.getInputStream();
                    bufferedReader = new BufferedReader(new InputStreamReader(stream));
                    StringBuffer Buffer = new StringBuffer();

                    String line= "";
                    while ((line = bufferedReader.readLine()) != null){
                        Buffer.append(line);
                    }

                    String completeJson = Buffer.toString();
                    JSONObject fullJsonCall = new JSONObject(completeJson);
                    JSONArray arrayMarcadores = fullJsonCall.getJSONArray("data");


                    //List<MarkerOptions> marcadoreslista = new ArrayList<MarkerOptions>();
                    ArrayList<MarcadoresDatos> marcadoresDatoses = new ArrayList<>();
                    for (int i=0;i<arrayMarcadores.length();i++){
                        MarcadoresDatos marcadoresDatos  = new MarcadoresDatos();
                        JSONObject marcadorFinal =  arrayMarcadores.getJSONObject(i);
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
                    if (connection != null){
                        connection.disconnect();
                    }

                    try {
                        if (bufferedReader != null){
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


           if (resultado == null){
                Snackbar.make(findViewById(android.R.id.content), "Verifica tu conexión a internet", Snackbar.LENGTH_LONG).show();
            }else {


                for (int i=0; i<resultado.size();i++){
                    MarcadoresDatos currentMarker = resultado.get(i);
                    Marker marcador = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(currentMarker.getLatitud(), currentMarker.getLongitud()))
                            .title(currentMarker.getTexto())
                            .icon(BitmapDescriptorFactory.fromBitmap(Marcadores(currentMarker.getCategorias())))

                    );
                    mapa.put(marcador.getId(), currentMarker);
                    String url = "http://test.grapot.co/s/i/"+String.valueOf(currentMarker.getIdMarker())+".jpg";
                    new DownloadImageTask(marcador.getId()).execute(url);

                }
             }}

        }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        private String mid;
        public DownloadImageTask(String pmid) {
            mid = pmid;
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

            if(result != null) {
                MarcadoresDatos m = mapa.get(mid);
                if (m!=null) {
                    m.setImg(result);
                }else{
                    Log.w("TAG", "wtf me hiciste cargar una imagen para un marker que ya no existe?");
                }

            }
        }
    }

    public Bitmap Marcadores(int marcadoresDatos){
        int height = 100;
        int width = 100;

        //Animales
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.dog);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap perroMarker = Bitmap.createScaledBitmap(b, width, height, false);

        //Ambientales
        BitmapDrawable bitmapambientales=(BitmapDrawable)getResources().getDrawable(R.drawable.ambientales);
        Bitmap b2=bitmapambientales.getBitmap();
        Bitmap ambientalMarker = Bitmap.createScaledBitmap(b2, width, height, false);

        //Policiales
        BitmapDrawable bitmapPoliciales=(BitmapDrawable)getResources().getDrawable(R.drawable.policiales);
        Bitmap b3=bitmapPoliciales.getBitmap();
        Bitmap policialesMarker = Bitmap.createScaledBitmap(b3, width, height, false);

        //Legales
        BitmapDrawable bitmapLegales=(BitmapDrawable)getResources().getDrawable(R.drawable.legales);
        Bitmap b4=bitmapLegales.getBitmap();
        Bitmap legalesMarker = Bitmap.createScaledBitmap(b4, width, height, false);

        //Servicios
        BitmapDrawable bitmapServicios=(BitmapDrawable)getResources().getDrawable(R.drawable.servicios);
        Bitmap b5=bitmapServicios.getBitmap();
        Bitmap serviciosMarker = Bitmap.createScaledBitmap(b5, width, height, false);

        //Sociales
        BitmapDrawable bitmapSociales=(BitmapDrawable)getResources().getDrawable(R.drawable.sociales);
        Bitmap b6=bitmapSociales.getBitmap();
        Bitmap socialesMarker = Bitmap.createScaledBitmap(b6, width, height, false);

        //Negocios
        BitmapDrawable bitmapNegocios=(BitmapDrawable)getResources().getDrawable(R.drawable.negocios);
        Bitmap b7=bitmapNegocios.getBitmap();
        Bitmap negociosMarker = Bitmap.createScaledBitmap(b7, width, height, false);

        //Emergencias
        BitmapDrawable bitmapEmergencias=(BitmapDrawable)getResources().getDrawable(R.drawable.emergencias);
        Bitmap b8=bitmapEmergencias.getBitmap();
        Bitmap emergenciasMaker = Bitmap.createScaledBitmap(b8, width, height, false);

        switch (marcadoresDatos){
            case 0:
                return perroMarker;
            case 1:
                return ambientalMarker;
            case 2:
                return policialesMarker;
            case 3:
                return legalesMarker;
            case 4:
                return serviciosMarker;
            case 5:
                return socialesMarker;
            case 6:
                return negociosMarker;
            case 7:
                return emergenciasMaker;
            default:
                return perroMarker;

        }

    }
}
