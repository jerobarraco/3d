package co.com.jonny.mapas1.controladores;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import co.com.jonny.mapas1.R;

/**
 * Created by Jonny on 22/05/2016.
 */
public class infoWindowMarker implements GoogleMap.InfoWindowAdapter {

    Context mContext;

    public infoWindowMarker(Context context){
        mContext = context;
    }
    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.info_marker_window, null);

        // Getting the position from the marker
        ImageView img = (ImageView) v.findViewById(R.id.tv_img);
        if (img == null){
            Log.e("TAG", "error");
        }

        TextView tvLat = (TextView) v.findViewById(R.id.tv_lat);

        // Getting reference to the TextView to set longitude
        TextView tvLng = (TextView) v.findViewById(R.id.tv_lng);

        int idn = 264; //tomar idn del marcador

        //DownloadImageTask dit = new DownloadImageTask(img);
        //       dit.execute("http://test.grapot.co/s/i/"+String.valueOf(idn)+".jpg");
        // Setting the latitude
        tvLat.setText("Latitude:" + marker.getPosition().latitude);

        // Setting the longitude
        tvLng.setText("Longitude:"+ marker.getPosition().longitude);

        // Returning the view containing InfoWindow contents
        return v;

    }
}
