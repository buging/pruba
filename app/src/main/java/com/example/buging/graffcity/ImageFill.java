package com.example.buging.graffcity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.github.snowdream.android.widget.SmartImageView;

/**
 * Created by Buging on 27-12-2015.
 */
public class ImageFill extends Activity {

    private SmartImageView foto_graffiti_fill;
    private String link_image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_fil);
        foto_graffiti_fill = (SmartImageView)findViewById(R.id.foto_graffiti_fill);

        Intent in = getIntent();
        Bundle bolsa = in.getExtras();
        link_image = bolsa.getString("link_image");

        new CargarImagenGraffiti().execute(link_image);
    }

    private class CargarImagenGraffiti extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            return urls[0];

        }
        /////////////////////////////////////////////////////////////////////////////////////////////////////
        //
        /////////////////////////////////////////////////////////////////////////////////////////////////////
        @Override
        protected void onPostExecute(String result) {
            try{
                Rect rect = new Rect(foto_graffiti_fill.getLeft(),foto_graffiti_fill.getTop(),foto_graffiti_fill.getRight(),foto_graffiti_fill.getBottom());
                foto_graffiti_fill.setImageUrl(result,rect);
            }catch(Exception e){
                Toast.makeText(getApplication(), "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
