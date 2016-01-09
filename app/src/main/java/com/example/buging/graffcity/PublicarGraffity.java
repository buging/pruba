package com.example.buging.graffcity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
//import com.google.android.gms.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Buging on 26-12-2015.
 */
public class PublicarGraffity extends Fragment{

    private String APP_DIRECTORY = "myPictureApp/";
    private String MEDIA_DIRECTORY = APP_DIRECTORY + "media";
    private String TEMPORAL_PICTURE_NAME = "temporal.jpg";


    private final int PHOTO_CODE = 100;
    private final int SELECT_PICTURE = 200;

    private ImageView imageView;

    private Cloudinary cloudinary;
    private JSONObject Result;
    private File file;
    private Button uploadImage;
    private String url_image = null;
    private String dir = null; //directorio

    private EditText et_nombre;
    private EditText et_descripcion;
    private Spinner s_ubicacion;
    private Spinner s_autor;

    private int id_autor;
    private int opcion_autor;
    private int opcion_ubicacion;
    private int obtener_gps = 1;
    private double latitud = 0;
    private double longitud = 0;
    private double lis_latitud = 0;
    private double lis_longitud = 0;
    private String nom;
    private String des;
    private LocationManager verificador_gps;

    private GoogleMap map;
    private MapView mapView;
    private Context context;
    private boolean realizarZoom = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_publicar_graffity, container, false);
        context = rootView.getContext();

        imageView = (ImageView) rootView.findViewById(R.id.setPicture);
        uploadImage = (Button) rootView.findViewById(R.id.uploadImage);
        imageView.setImageResource(R.drawable.agregar_image);
        et_nombre = (EditText) rootView.findViewById(R.id.et_nombre_graffiti);
        et_descripcion = (EditText) rootView.findViewById(R.id.et_descripcion_graffiti);
        s_ubicacion = (Spinner) rootView.findViewById(R.id.spinnerUbicacion);
        s_autor = (Spinner) rootView.findViewById(R.id.spinnerAutor);

        String[] ubicaciones = {"GPS", "Anonima"};

        SharedPreferences settings = getActivity().getSharedPreferences("session", 0);
        String a = settings.getString("nickName", "").toString();
        String[] autores = {a, "Anonimo"};

        opcion_autor = 0;
        opcion_ubicacion = 0;

        id_autor = settings.getInt("id", 0);

        /////////////////////////////////////////////////////////////////////////////////
        mapView = (MapView) rootView.findViewById(R.id.mapa_salvacion);
        mapView.onCreate(savedInstanceState);
        map = mapView.getMap();
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setMyLocationEnabled(true);

        //ver si gps esta a la escucha
        verificador_gps = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        ////////////////////////////////////////////////////////////////////////////////

        SystemUtilities su = new SystemUtilities(getActivity().getApplicationContext());
        final String internet;
        if(su.isNetworkAvailable()){
            internet = "si";
        }else{
            internet = "no";
        }

        if(internet == "si") {
            if (verificador_gps.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);//creamos un zoom para ver el mapa
                map.animateCamera(zoom);//aplicamos el zoom al mapa
                map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                    @Override
                    public void onMyLocationChange(Location location) {
                        //se obtiene la latitud y longitud indicada por el gps
                        lis_latitud = location.getLatitude();
                        lis_longitud = location.getLongitude();
                        SystemClock.sleep(1000);

                        if (realizarZoom == false && lis_latitud != 0 && lis_longitud != 0) {
                            realizarZoom = true;

                            CameraUpdate cam = CameraUpdateFactory.newLatLng(new LatLng(lis_latitud, lis_longitud));
                            map.animateCamera(cam);//realizar ajuste de pantalla

                            map.setOnMyLocationChangeListener(null); //se termina el proceso de escucha del gps tan solo se realiza una vez

                            Toast.makeText(getActivity(), "Mapa se esta posicionando", Toast.LENGTH_LONG).show();

                        }


                    }
                });
            } else {
                //******  HERE's the PROBLEM  ********
                Toast.makeText(getActivity(), "Debe activar GPS", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(getActivity(), "No tiene conexión a internet", Toast.LENGTH_LONG).show();
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] options = {"Tomar foto", "Elegir de galeria", "Cancelar"};
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Abrir desde");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int seleccion) {
                        if (options[seleccion] == "Tomar foto") {
                            openCamera();
                        } else if (options[seleccion] == "Elegir de galeria") {
                            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.setType("image/*");
                            startActivityForResult(intent.createChooser(intent, "Selecciona app de imagen"), SELECT_PICTURE);
                        } else if (options[seleccion] == "Cancelar") {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_nombre.getText().toString().length()<=3){
                    Toast.makeText(getActivity(), "Nombre de graffity no ingresado", Toast.LENGTH_LONG).show();
                    return;
                }else if(et_descripcion.getText().toString().length()<=3) {
                    Toast.makeText(getActivity(), "Descripción de graffity no ingresada", Toast.LENGTH_LONG).show();
                    return;
                }
                uploadImage.setEnabled(false);
                Toast.makeText(getActivity(), "Publicando graffity", Toast.LENGTH_LONG).show();
                nom = et_nombre.getText().toString();
                des = et_descripcion.getText().toString();
                HashMap config = new HashMap();
                config.put("cloud_name", "graffcity");
                config.put("api_key", "123528223989847");//I have changed the key and secret
                config.put("api_secret", "Qug_jj6le_fu5Gig89IAnD-sHhQ");
                cloudinary = new Cloudinary(config);
                new UploadImage(getActivity().getApplicationContext()).execute();
            }
        });

        s_ubicacion.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, ubicaciones));
        s_ubicacion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

                if (position == 0) {
                    //y se realiza la busqueda de los lugares cercanos
                    SystemUtilities su = new SystemUtilities(getActivity().getApplicationContext());
                    final String internet;
                    if (su.isNetworkAvailable()) {
                        internet = "si";
                    } else {
                        internet = "no";
                    }

                    if (internet == "si") {
                        if (verificador_gps.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            latitud = lis_latitud;
                            longitud = lis_longitud;
                            opcion_ubicacion = 0;
                        } else {
                            //******  HERE's the PROBLEM  ********
                            latitud = 0;
                            longitud = 0;
                            opcion_ubicacion = 1;
                            Toast.makeText(getActivity(), "Debe activar GPS", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "No tiene conexión a internet", Toast.LENGTH_LONG).show();
                        opcion_ubicacion = 1;
                    }
                } else if (position == 1) {
                    opcion_ubicacion = 1;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                latitud = lis_latitud;
                longitud = lis_longitud;
                opcion_ubicacion = 0;
            }
        });

        s_autor.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, autores));
        s_autor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

                if (position == 0) {
                    opcion_autor = 0;
                } else if (position == 1) {
                    opcion_autor = 1;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                opcion_autor = 0;
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    private void openCamera() {
        File file = new File(Environment.getExternalStorageDirectory(), MEDIA_DIRECTORY);
        file.mkdirs();

        dir = Environment.getExternalStorageDirectory() + File.separator
                + MEDIA_DIRECTORY + File.separator + TEMPORAL_PICTURE_NAME;

        File newFile = new File(dir);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newFile));
        startActivityForResult(intent, PHOTO_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PHOTO_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    dir = Environment.getExternalStorageDirectory() + File.separator
                            + MEDIA_DIRECTORY + File.separator + TEMPORAL_PICTURE_NAME;
                    decodeBitmap(dir);
                }
                break;

            case SELECT_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri path = data.getData();
                    dir = getRealPathFromURI(getActivity(), path);
                    imageView.setImageURI(path);
                }
                break;
        }

    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void decodeBitmap(String ruta) {
        dir = ruta;
        Bitmap bitmap;
        bitmap = BitmapFactory.decodeFile(ruta);

        imageView.setImageBitmap(bitmap);
    }


    private class UploadImage extends AsyncTask<String, Void, String> {

        private Context context;
        private double lat;
        private double lon;
        private int id_au;

        public UploadImage(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... urls) {
            String response = "";

            if(dir==null){
                return "Imagen no seleccionada";
            }

            try {
                file = new File(dir);
                Result = new JSONObject(cloudinary.uploader().upload(file, ObjectUtils.emptyMap()));
                if(opcion_ubicacion == 1){
                    lat = 0;
                    lon = 0;
                }else if(opcion_ubicacion  == 0){
                    lat = lis_latitud;
                    lon = lis_longitud;
                }

                if(opcion_autor == 0){
                    id_au = id_autor;
                }else if(opcion_autor == 1){
                    id_au = 1;
                }

                url_image = Result.optString("url");
                Consultas c = new Consultas();
                return c.publicarGraffitis("http://192.168.42.146:8080/graffcity/graffiti", id_au, 1, nom, des, lat, lon, url_image, 0, false);


            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
            uploadImage.setEnabled(true);
        }


    }
}
