package com.gedesoft.ddd.vistas;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.gedesoft.ddd.R;
import com.gedesoft.ddd.modelos.MarcadoresAsync;
import com.gedesoft.ddd.modelos.MarcadoresDatos;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Hashtable;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, android.location.LocationListener {
    private static final int CODIGO_SOLICITUD_LOCATION = 1;
    private static final int CODIGO_SOLICITUD_COARSEFINE = 2;
    private GoogleMap mMap;
    private boolean mapReady = false;
    private double lat;
    private double lng;
    private LocationManager mLocationManager;
    private Marker marcador;
    private double norte;
    private double este;
    private double sur;
    private double oeste;
    private int acc;
    private int categoria=-1;
    private FloatingActionButton mFloatingActionButton;
    private Marker lastOpenned = null;
    private ImageView imgInfoWindow;
    private Hashtable<String, MarcadoresDatos> mapa = new Hashtable<>();

    //bundle info facebook
    String id;
    String name;
    String surname;
    String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        //Datos de Facebook

        Bundle inBundle = getIntent().getExtras();
        if (inBundle == null){

        }else {
            id = inBundle.get("idprofile").toString();
            name = inBundle.get("name").toString();
            surname = inBundle.get("surname").toString();
            imageUrl = inBundle.get("imageUrl").toString();
        }


        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.fab);

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(MainActivity.this, Categorias.class);
                intent2.putExtra("Latitud", lat);
                intent2.putExtra("Longitud", lng);
                intent2.putExtra("Acc", acc);
                startActivity(intent2);
            }
        });


        setSupportActionBar(toolbar);
        CheckEnableGPS();


        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET
                }, CODIGO_SOLICITUD_COARSEFINE);
                return;
            }
        } else {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 7000, 10, this); // TODO 1
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_Fragment);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    /**
     * Método que nos comunica si el mapa ya está listo y ejecuta las cosas solo cuando lo está.
     *
     * @param googleMap
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapReady = true;
        mMap = googleMap;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, CODIGO_SOLICITUD_LOCATION);


            }
            Iniciar();
        } else {
            Iniciar();
        }
    }
    /**
     * Método para verificar qué pasa cuando se pidió el permiso en runtime para habilitar el GPS (Android M,6,23)
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CODIGO_SOLICITUD_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(findViewById(android.R.id.content), R.string.permisos_result_main, Snackbar.LENGTH_SHORT).show();
                }
            case CODIGO_SOLICITUD_COARSEFINE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(findViewById(android.R.id.content), R.string.permisos_result_main, Snackbar.LENGTH_SHORT).show();
                }

        }
    }

    /**
     * Método para saber si el GPS está encendido o no y ayudar al usuario a encenderlo
     */
    private void CheckEnableGPS() {
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!enabled) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.titulo_gps_no_activo_main))
                    .setCancelable(false)
                    .setMessage(getString(R.string.activar_gps_main))
                    .setNegativeButton(getString(R.string.salir), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setPositiveButton(getString(R.string.activar), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            //ComponentName cn = intent.getComponent();
                            //Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
                            startActivity(intent);

                        }
                    });
            builder.show();
        }

    }

    //MÉTODOS LOCATION LISTENER
    @Override
    public void onLocationChanged(Location location) {
        LatLngBounds curScreen = mMap.getProjection().getVisibleRegion().latLngBounds;

        norte = curScreen.northeast.latitude;
        este = curScreen.northeast.longitude;
        sur = curScreen.southwest.latitude;
        oeste = curScreen.southwest.longitude;
        double olat = lat, olng = lng;

        lat = location.getLatitude();
        lng = location.getLongitude();
        acc = (int) location.getAccuracy();

        if (olat == 0 || olng == 0) {
            LatLng inicial = new LatLng(lat, lng);
            CameraPosition target = CameraPosition.builder()
                    .target(inicial)
                    .zoom(15)
                    .bearing(360)
                    .tilt(40)
                    .build();

            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(target));
        }

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





    public void Iniciar() {
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, CODIGO_SOLICITUD_LOCATION);


                // return;
            }
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
                    MarcadoresAsync marcadoresAsync = new MarcadoresAsync(mapReady, mMap, MainActivity.this, mapa);
                    marcadoresAsync.execute("http://test.grapot.co/q.php?cat=" + categoria + "&n=" + norte + "&e=" + este + "&s=" + sur + "&w=" + oeste);
                }
            });

            LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
            boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (!enabled) {

            } else {
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Criteria criteria = new Criteria();//3

                Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
                if (location != null) {
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
                    public View getInfoContents(final Marker arg0) {

                        // Getting view from the layout file info_window_layout
                        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View v = inflater.inflate(R.layout.info_marker_window, null);

                        // Getting the position from the marker
                        imgInfoWindow = (ImageView) v.findViewById(R.id.tv_img);

                        if (imgInfoWindow == null) {
                            Log.e("TAG", "error");
                        }

                        String mid = arg0.getId();

                        if (mapa.containsKey(mid)) {
                            MarcadoresDatos md = mapa.get(mid);

                            Picasso.with(MainActivity.this).load("http://test.grapot.co/s/i/" + String.valueOf(md.getIdMarker()) + ".jpg")
                                    //.error(R.drawable.error_placeholder_image)
                                    //.networkPolicy(NetworkPolicy.OFFLINE)
                                    .into(imgInfoWindow, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            //Log.e("CARGA", "Imagen cargada");

                                        }

                                        @Override
                                        public void onError() {
                                        }
                                    });
                            // imgInfoWindow.setImageBitmap(md.getImg());
                        } else {
                            Log.i("MIDERROR", "el marker " + mid + " no tiene foto");
                        }

                        TextView tvLat = (TextView) v.findViewById(R.id.tv_lat);
                        tvLat.setText(arg0.getTitle());
                        // Getting reference to the TextView to set longitude
                        TextView tvLng = (TextView) v.findViewById(R.id.tv_lng);


                        return v;

                    }
                });


            }
        }else{
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
                    MarcadoresAsync marcadoresAsync = new MarcadoresAsync(mapReady, mMap, MainActivity.this, mapa);
                    marcadoresAsync.execute("http://test.grapot.co/q.php?cat=" + categoria + "&n=" + norte + "&e=" + este + "&s=" + sur + "&w=" + oeste);
                }
            });

            LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
            boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (!enabled) {

            } else {
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Criteria criteria = new Criteria();//3

                Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
                if (location != null) {
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
                    public View getInfoContents(final Marker arg0) {

                        // Getting view from the layout file info_window_layout
                        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View v = inflater.inflate(R.layout.info_marker_window, null);

                        // Getting the position from the marker
                        imgInfoWindow = (ImageView) v.findViewById(R.id.tv_img);

                        if (imgInfoWindow == null) {
                            Log.e("TAG", "error");
                        }

                        String mid = arg0.getId();

                        if (mapa.containsKey(mid)) {
                            MarcadoresDatos md = mapa.get(mid);

                            Picasso.with(MainActivity.this).load("http://test.grapot.co/s/i/" + String.valueOf(md.getIdMarker()) + ".jpg")
                                    //.error(R.drawable.error_placeholder_image)
                                    //.networkPolicy(NetworkPolicy.OFFLINE)
                                    .into(imgInfoWindow, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            //Log.e("CARGA", "Imagen cargada");

                                        }

                                        @Override
                                        public void onError() {
                                        }
                                    });
                            // imgInfoWindow.setImageBitmap(md.getImg());
                        } else {
                            Log.i("MIDERROR", "el marker " + mid + " no tiene foto");
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.ddd_menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

         int id = item.getItemId();

        if (id == R.id.action_perfil) {
            Intent intent = new Intent(this, Perfil.class);
            intent.putExtra(Perfil.NAME, name);
            intent.putExtra(Perfil.SURNAME, surname);
            intent.putExtra(Perfil.ID, this.id);
            intent.putExtra(Perfil.IMG, imageUrl);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_logout) {
            logout();
            return true;
        }

        if (id== R.id.action_filter){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater factory = LayoutInflater.from(this);
            final View view = factory.inflate(R.layout.menu_filter_layout, null);
            builder.setView(view);
            builder.setTitle("Selecciona la categoría a filtrar");
            builder.setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });


            //Aquí guardo la selección del usuario
            RadioGroup group = (RadioGroup) view.findViewById(R.id.rdgGrupo);
            SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = sharedPreferences.edit();
            SharedPreferences saved = getPreferences(Context.MODE_PRIVATE);
            int radioId=sharedPreferences.getInt("check", 0);
            if(radioId>0){
                RadioButton rbtn=(RadioButton)view.findViewById(radioId);
                rbtn.setChecked(true);
            }
            //fin

            group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {

                    switch (checkedId){
                        case R.id.radio_button_animales:
                            editor.putInt("check", checkedId);
                            categoria = 1;
                            refrescarMapa();
                            break;
                        case R.id.radio_button_ambientales:
                            categoria = 2;
                            editor.putInt("check", checkedId);
                            refrescarMapa();
                            break;
                        case R.id.radio_button_policiales:
                            categoria = 3;
                            editor.putInt("check", checkedId);
                            refrescarMapa();
                            break;
                        case R.id.radio_button_legales:
                            categoria = 4;
                            editor.putInt("check", checkedId);
                            refrescarMapa();
                            break;
                        case R.id.radio_button_servicios:
                            categoria = 5;
                            editor.putInt("check", checkedId);
                            refrescarMapa();
                            break;
                        case R.id.radio_button_sociales:
                            categoria = 6;
                            editor.putInt("check", checkedId);
                            refrescarMapa();
                            break;
                        case R.id.radio_button_negocios:
                            categoria = 7;
                            editor.putInt("check", checkedId);
                            refrescarMapa();
                            break;
                        case R.id.radio_button_emergencias:
                            categoria = 8;
                            editor.putInt("check", checkedId);
                            refrescarMapa();
                            break;
                        case R.id.radio_button_todos:
                            categoria = -1;
                            editor.putInt("check", checkedId);
                            refrescarMapa();
                            break;
                    }
                    editor.apply();
                }
            });

            AlertDialog levelDialog  = builder.create();
            levelDialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void logout(){
        LoginManager.getInstance().logOut();
        Intent login = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(login);
        finish();
    }

    public void refrescarMapa(){
        MarcadoresAsync marcadoresAsync = new MarcadoresAsync(mapReady, mMap, MainActivity.this, mapa);
        marcadoresAsync.execute("http://test.grapot.co/q.php?cat=" + categoria + "&n=" + norte + "&e=" + este + "&s=" + sur + "&w=" + oeste);
    }
}

