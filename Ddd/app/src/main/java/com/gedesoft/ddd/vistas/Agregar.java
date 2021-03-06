package com.gedesoft.ddd.vistas;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.facebook.FacebookSdk;
import com.gedesoft.ddd.R;
import com.gedesoft.ddd.modelos.PostAsync;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Locale;

public class Agregar extends AppCompatActivity {

    private Button enviar, bFoto, cancelar, hablar;
    private EditText mEditText;
    final static int REQUEST_IMAGE_CAMERA = 1;
    private ImageView pic;
    private String encodedImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_agregar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarAgregar);
        setSupportActionBar(toolbar);

        enviar = (Button) findViewById(R.id.button_enviar);
        cancelar = (Button) findViewById(R.id.button_cancelar);
        mEditText = (EditText) findViewById(R.id.editText_Descrip);
        bFoto = (Button) findViewById(R.id.button_foto);
        pic = (ImageView) findViewById(R.id.Imagen_agregar);
        hablar = (Button) findViewById(R.id.btnSpech);

        hablar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requerirHablar();
            }
        });

        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pic.setImageBitmap(null);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_IMAGE_CAMERA);
            }
        });

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Agregar.this)
                        .setMessage(getString(R.string.cancelar_envio_agregar))
                        .setPositiveButton(getString(R.string.aceptar), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //dialog.dismiss();
                                finish();
                            }
                        })
                        .setNegativeButton(getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.show();
            }
        });

        if (!hasCamera()){
            bFoto.setEnabled(false);
        }


        Bundle bundle = getIntent().getExtras();
        String nombre = bundle.getString("NOMBRE");
        final double lati = bundle.getDouble("Latitud");
        final double longi = bundle.getDouble("Longitud");
        final int acc = bundle.getInt("Acc");
        final int cate = bundle.getInt("Categoria");
        final int idPhP = bundle.getInt("IDPHP");
        final String utk = bundle.getString("UTK");


        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String textoMarcador = mEditText.getText().toString();
                    if (mEditText.getText().toString().isEmpty()) {
                        mEditText.setError(getString(R.string.error_campo_obligatorio_agregar));
                    }else if(pic.getDrawable() == null) {
                        Snackbar.make(findViewById(android.R.id.content), R.string.imagen_obligatoria_agregar, Snackbar.LENGTH_SHORT).show();
                    }else {
                        String method = "info";
                        PostAsync postAsync = new PostAsync(Agregar.this, Agregar.this);
                        postAsync.execute(method, textoMarcador, Double.toString(lati), Double.toString(longi), Integer.toString(acc), encodedImage, Integer.toString(cate)
                        ,Integer.toString(idPhP), utk);

                        mEditText.setText("");

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                finally {
                    InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);

                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }


            }
        });

        bFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pic.setImageBitmap(null);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_IMAGE_CAMERA);
            }
        });


        setTitle(getString(R.string.titulo_agregar_activity)+ nombre);
    }


    public void requerirHablar(){
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.agregar_habla_ahora));

        try{
            startActivityForResult(i, 100);
        }catch (ActivityNotFoundException e){
            e.printStackTrace();
            Snackbar.make(findViewById(android.R.id.content), R.string.agregar_error_funcion_hablar, Snackbar.LENGTH_SHORT).show();
        }

    }

    private boolean hasCamera() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
        }else {
            return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==REQUEST_IMAGE_CAMERA && resultCode== RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap photo = (Bitmap) extras.get("data");
            pic.setImageBitmap(photo);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 90, baos);
            byte[] imageBytes =  baos.toByteArray();
            encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        }

        if (requestCode==100 && resultCode==RESULT_OK && data != null){

            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            mEditText.setText(result.get(0));
        }
    }



}

