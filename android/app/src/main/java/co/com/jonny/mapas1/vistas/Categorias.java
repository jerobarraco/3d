package co.com.jonny.mapas1.vistas;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;

import co.com.jonny.mapas1.R;
import co.com.jonny.mapas1.controladores.AdaptadorCategorias;

public class Categorias extends AppCompatActivity implements AdaptadorCategorias.myOnclick{


    private RecyclerView mRecyclerView;
    private ArrayList<co.com.jonny.mapas1.modelos.Categorias> mCategoriases = new ArrayList<>();
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

        mCategoriases.add(new co.com.jonny.mapas1.modelos.Categorias("Animales", R.drawable.dog, 0));
        mCategoriases.add(new co.com.jonny.mapas1.modelos.Categorias("Ambientales", R.drawable.ambientales, 1));
        mCategoriases.add(new co.com.jonny.mapas1.modelos.Categorias("Policiales", R.drawable.policiales, 2));
        mCategoriases.add(new co.com.jonny.mapas1.modelos.Categorias("Legales", R.drawable.legales, 3));
        mCategoriases.add(new co.com.jonny.mapas1.modelos.Categorias("Servicios", R.drawable.servicios, 4));
        mCategoriases.add(new co.com.jonny.mapas1.modelos.Categorias("Sociales", R.drawable.sociales, 5));
        mCategoriases.add(new co.com.jonny.mapas1.modelos.Categorias("Negocios", R.drawable.negocios, 6));
        mCategoriases.add(new co.com.jonny.mapas1.modelos.Categorias("Emergencias", R.drawable.emergencias, 7));

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_lista_categorias);

        AdaptadorCategorias adapter = new AdaptadorCategorias(this, mCategoriases, this);
        RecyclerView.LayoutManager manager = new GridLayoutManager(this, 3);

        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(adapter);



    }

    @Override
    public void onClick(AdaptadorCategorias.ViewHolderCates viewHolderCates, int id) {

        co.com.jonny.mapas1.modelos.Categorias cateActual = mCategoriases.get(id);
        if (cateActual.getNombre().isEmpty() || cateActual.getCategoria() > 7){
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("Información inválida")
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
