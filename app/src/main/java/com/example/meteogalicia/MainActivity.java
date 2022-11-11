package com.example.meteogalicia;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.meteogalicia.NoModificar.MeteoGaliciaCortoPlazo;
import com.example.meteogalicia.NoModificar.MeteoGaliciaCortoPlazo.LocalidadMeteoGalicia;
import com.example.meteogalicia.NoModificar.Utilidades;

public class MainActivity extends AppCompatActivity {

    private final LocalidadMeteoGalicia[] localidades = MeteoGaliciaCortoPlazo.getLocalidades();
    // Variables útiles para la ventana de diálogo:
    private int ultimaLocalidadSeleccionada = -1;
    private Button button_localidad;
    private ImageButton imageButton_telefono;
    private WebView webView_pronostico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_localidad = this.findViewById(R.id.button_localidad);
        imageButton_telefono = findViewById(R.id.imageButton_telefono);
        webView_pronostico = findViewById(R.id.webView_pronostico);

        imageButton_telefono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:+34881999654")));
            }
        });

        button_localidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayAdapter adapter = new ArrayAdapter(MainActivity.this, android.R.layout.select_dialog_singlechoice, localidades);
                AlertDialog.Builder dialog_localidad = new AlertDialog.Builder(MainActivity.this);

                dialog_localidad.setTitle("Seleccione localidad");

                dialog_localidad.setSingleChoiceItems(adapter, ultimaLocalidadSeleccionada, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Task task = new Task();
                        ultimaLocalidadSeleccionada = i;
                        task.execute(localidades[i]);
                        dialogInterface.dismiss();
                    }
                });

                dialog_localidad.show();
            }
        });

    }

    // Evita cerrar accidentalmente la Activity
    @Override
    public void onBackPressed() {
        //  super.onBackPressed(); Importante este comentario para que no se ejecute finish

        AlertDialog.Builder datosDialog = new AlertDialog.Builder(this);
        datosDialog.setCancelable(false);
        datosDialog.setTitle("Atención");
        datosDialog.setMessage("¿Realmente desea salir de la aplicación?");
        datosDialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        datosDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        datosDialog.create().show();

    }

    class Task extends AsyncTask<LocalidadMeteoGalicia, Void, String> {

        ProgressDialog progressDialog;

        @Override

        protected void onPreExecute() {
            super.onPreExecute();
            if (!Utilidades.existeConexionInternet(MainActivity.this)) {
                cancel(true);
            }
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Descargando...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(LocalidadMeteoGalicia... localidades) {
            return MeteoGaliciaCortoPlazo.obtenPronostico(MainActivity.this, localidades[0]);
        }

        @Override
        protected void onPostExecute(String url) {
            super.onPostExecute(url);
            webView_pronostico.loadUrl("file:///" + url);
            progressDialog.dismiss();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Toast.makeText(MainActivity.this, "Error conexion", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }
    }


}
