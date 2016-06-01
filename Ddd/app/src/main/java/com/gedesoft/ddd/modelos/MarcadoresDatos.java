package com.gedesoft.ddd.modelos;

import android.graphics.Bitmap;

/**
 * Created by Jonny on 29/05/2016.
 */
public class MarcadoresDatos {

    private   double latitud, longitud;
    private String texto;
    private int idMarker;
    private int categorias;

    public final Bitmap getImg() {
        return img;
    }

    public void setImg(final Bitmap img) {
        this.img = img;
    }

    private Bitmap img;

    public int getIdMarker() {
        return idMarker;
    }

    public void setIdMarker(int idMarker) {
        this.idMarker = idMarker;
    }



    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public int getCategorias() {
        return categorias;
    }

    public void setCategorias(int categorias) {
        this.categorias = categorias;
    }
}
