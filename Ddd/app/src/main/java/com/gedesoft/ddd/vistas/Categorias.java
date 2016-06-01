package com.gedesoft.ddd.vistas;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.gedesoft.ddd.R;
import com.gedesoft.ddd.controladores.AdaptadorCategorias;

import java.util.ArrayList;

public class Categorias extends AppCompatActivity implements AdaptadorCategorias.myOnclick{


    private RecyclerView mRecyclerView;
    private ArrayList<com.gedesoft.ddd.modelos.Categorias> mCategoriases = new ArrayList<>();
    double Latitud;
    double Longitud;
    int acc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorias);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarCategorias);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Categorías");

        Bundle bundle = getIntent().getExtras();
        if (bundle == null){

        }else {
            Latitud = bundle.getDouble("Latitud");
            Longitud = bundle.getDouble("Longitud");
            acc = bundle.getInt("Acc");


        }

        mCategoriases.add(new com.gedesoft.ddd.modelos.Categorias("Animales", R.drawable.dog, 1));
        mCategoriases.add(new com.gedesoft.ddd.modelos.Categorias("Ambientales", R.drawable.ambientales, 2));
        mCategoriases.add(new com.gedesoft.ddd.modelos.Categorias("Policiales", R.drawable.policiales, 3));
        mCategoriases.add(new com.gedesoft.ddd.modelos.Categorias("Legales", R.drawable.legales, 4));
        mCategoriases.add(new com.gedesoft.ddd.modelos.Categorias("Servicios", R.drawable.servicios, 5));
        mCategoriases.add(new com.gedesoft.ddd.modelos.Categorias("Sociales", R.drawable.sociales, 6));
        mCategoriases.add(new com.gedesoft.ddd.modelos.Categorias("Negocios", R.drawable.negocios, 7));
        mCategoriases.add(new com.gedesoft.ddd.modelos.Categorias("Emergencias", R.drawable.emergencias, 8));

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_lista_categorias);

        AdaptadorCategorias adapter = new AdaptadorCategorias(this, mCategoriases, this);
        RecyclerView.LayoutManager manager = new GridLayoutManager(this, 3);

        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(adapter);



    }

    @Override
    public void onClick(AdaptadorCategorias.ViewHolderCates viewHolderCates, int id) {

        com.gedesoft.ddd.modelos.Categorias cateActual = mCategoriases.get(id);
        if (cateActual.getNombre().isEmpty() || cateActual.getCategoria() > 8){
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("Información inválida")
                    .setCancelable(false)
                    .setMessage("No has elegido una categoría de manera correcta. Intenta de nuevo")
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    });
            builder.show();

        }else {
            Intent intent = new Intent(this, Agregar.class);
            intent.putExtra("NOMBRE", cateActual.getNombre());
            intent.putExtra("Latitud", Latitud);
            intent.putExtra("Longitud", Longitud);
            intent.putExtra("Acc", acc);
            intent.putExtra("Categoria", cateActual.getCategoria());
            startActivity(intent);
            finish();
        }




    }
}

