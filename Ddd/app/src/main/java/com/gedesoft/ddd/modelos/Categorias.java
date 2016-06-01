package com.gedesoft.ddd.modelos;

/**
 * Created by Jonny on 29/05/2016.
 */
public class Categorias {

    private String nombre;
    private int Imagen;
    private int categoria;

    public Categorias(String nombre, int imagen, int categoria){
        this.nombre = nombre;
        Imagen = imagen;
        this.categoria = categoria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getImagen() {
        return Imagen;
    }

    public void setImagen(int imagen) {
        Imagen = imagen;
    }

    public int getCategoria() {
        return categoria;
    }

    public void setCategoria(int categoria) {
        this.categoria = categoria;
    }
}
