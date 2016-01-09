package com.example.buging.graffcity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.snowdream.android.widget.SmartImageView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Buging on 27-12-2015.
 */
public class VerGraffiti  extends AppCompatActivity {

    private TextView tv_nombre;
    private TextView tv_descripcion;
    private SmartImageView foto_graffiti;
    private RatingBar rb_puntuacion;
    private String id_graffiti;
    private String nombre_graffiti;
    private String descripcion_graffiti;
    private String link_graffiti;
    private int id_usuario;
    private boolean tiene_cal;
    private float calificacion;
    private int update_calificacion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_ver_graffiti);
        tv_nombre = (TextView) findViewById(R.id.tv_nombre);
        tv_descripcion = (TextView) findViewById(R.id.tv_descripcion);
        foto_graffiti = (SmartImageView) findViewById(R.id.foto_graffiti);
        rb_puntuacion = (RatingBar) findViewById(R.id.valoracion);

        tiene_cal = false;
        calificacion = 0;

        //rb_puntuacion.setEnabled(true);
        //rb_puntuacion.setClickable(true);
        //rb_puntuacion.setRating(2);

        rb_puntuacion.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating,  boolean fromUser)  {
                //rb_puntuacion.setClickable(false);
                if(rating!=calificacion) {
                    rb_puntuacion.setEnabled(false);
                    update_calificacion = (int) rating;
                    new CargarDatosGraffiti().execute("calificar");

                }
            }
        });

        foto_graffiti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ImageFill.class);
                Bundle bolsa = new Bundle();
                bolsa.putString("link_image", link_graffiti);
                intent.putExtras(bolsa);
                startActivity(intent);
            }
        });

        //se extraer datos de graffiti
        Intent in = getIntent();
        Bundle bolsa = in.getExtras();
        id_graffiti = bolsa.getString("id");
        nombre_graffiti = bolsa.getString("nombre");
        descripcion_graffiti = bolsa.getString("descripcion");
        link_graffiti = bolsa.getString("link");
        SharedPreferences settings = getApplicationContext().getSharedPreferences("session", 0);
        id_usuario = settings.getInt("id", 0);

        new CargarDatosGraffiti().execute("foto");
        //new CargarDatosGraffiti().execute("pintar");
    }

    private void setNombre(String info) {

        tv_nombre.setText(info);
        tv_nombre.setMovementMethod(new ScrollingMovementMethod());
    }

    private void setDescripcion(String info) {

        tv_descripcion.setText(info);
        tv_descripcion.setMovementMethod(new ScrollingMovementMethod());
    }

    private class CargarDatosGraffiti extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            if (urls[0] == "foto") {
                Consultas c = new Consultas();
                calificacion = c.tieneCalificacion(id_graffiti,Integer.toString(id_usuario));
                return "foto";
            }else if (urls[0] == "calificar") {
                Consultas c = new Consultas();
                Boolean s = c.calificar(tiene_cal, Integer.toString(id_usuario), id_graffiti, Integer.toString(update_calificacion));
                if(s==true){
                    return "correcto";
                }else{
                    return "fallido";
                }
            }
            return "";

        }
        /////////////////////////////////////////////////////////////////////////////////////////////////////
        //
        /////////////////////////////////////////////////////////////////////////////////////////////////////
        @Override
        protected void onPostExecute(String result) {
            if(result == "foto") {
                try {
                    Rect rect = new Rect(foto_graffiti.getLeft(), foto_graffiti.getTop(), foto_graffiti.getRight(), foto_graffiti.getBottom());
                    foto_graffiti.setImageUrl(link_graffiti, rect);
                    if(calificacion==-1){
                        calificacion = 0;
                        rb_puntuacion.setRating(0);
                        tiene_cal = false;
                    }else{
                        rb_puntuacion.setRating(calificacion);
                        tiene_cal = true;
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplication(), "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
                }

                setNombre(nombre_graffiti);
                setDescripcion(descripcion_graffiti);
            }else if(result == "correcto"){
                tiene_cal = true;
                rb_puntuacion.setEnabled(true);
                rb_puntuacion.setRating(update_calificacion);
                calificacion = (float)update_calificacion;
                Toast.makeText(getApplication(), "Calificación realizada con exito", Toast.LENGTH_SHORT).show();
            }else if(result == "fallido"){
                rb_puntuacion.setEnabled(true);
                rb_puntuacion.setRating(update_calificacion);
                Toast.makeText(getApplication(), "Error al realizar la calificación", Toast.LENGTH_SHORT).show();

            }
        }
    }

}
