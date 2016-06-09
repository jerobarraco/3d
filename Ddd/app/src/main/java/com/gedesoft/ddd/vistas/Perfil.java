package com.gedesoft.ddd.vistas;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gedesoft.ddd.R;
import com.squareup.picasso.Picasso;

public class Perfil extends AppCompatActivity {

    public static final String NAME = "Name";
    public static final String SURNAME ="Surname";
    public static final String ID = "ID";
    public static final String IMG = "Imagen";
    public static final String TOKEN = "Token";
    public static final String USERID = "UserID";


    private TextView txtName;
    private TextView txtID, txtToken, txtUserId;
    private ImageView mImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarPerfil);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.tu_perfil);

        txtName = (TextView) findViewById(R.id.txtPerfilNombre);
        txtID = (TextView) findViewById(R.id.txtPerfilId);
        mImageView = (ImageView) findViewById(R.id.txtPerfilImagen);
        txtToken = (TextView) findViewById(R.id.txtPerfilToken);
        txtUserId = (TextView) findViewById(R.id.txtPerfilUserID);

        Bundle inBundle = getIntent().getExtras();
        if (inBundle.containsKey(Perfil.NAME) && inBundle.containsKey(Perfil.SURNAME) && inBundle.containsKey(Perfil.ID)
                && inBundle.containsKey(Perfil.IMG) && inBundle != null){
            String ide = inBundle.get(Perfil.ID).toString();
            String name = inBundle.get(Perfil.NAME).toString();
            String surname = inBundle.get(Perfil.SURNAME).toString();
            String imageUrl = inBundle.get(Perfil.IMG).toString();


            txtName.setText("Nombre: " + name + " " + surname);
            txtID.setText("Id cuenta " + " " + ide);
            Picasso.with(this).load(imageUrl).into(mImageView);

        }else {
            Toast.makeText(Perfil.this, "Vacío", Toast.LENGTH_SHORT).show();
        }


    }
}
