package com.example.buging.graffcity;


import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Mapa extends Fragment {

    private GoogleMap map;
    private MapView mapView;
    private double latitud, longitud; //se guarda la latitud y longitud del lugar actual
    private boolean realizarZoom = false;
    private LocationManager verificador_gps = null;
    private Context context;
    private List<Marker> markerList;
    private List<String> id_graffiti;
    private List<String> nombre_graffiti;
    private List<String> descripcion_graffiti;
    private List<String> link_graffiti;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //SystemClock.sleep(10000);
        View rootView = inflater.inflate(R.layout.layout_ver_mapa, container, false);
        context = rootView.getContext();

        mapView = (MapView) rootView.findViewById(R.id.mapa);
        mapView.onCreate(savedInstanceState);
        map = mapView.getMap();
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setMyLocationEnabled(true);

        //ver si gps esta a la escucha
        verificador_gps = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);

        //inicializar arrayList
        markerList  = new ArrayList<Marker>(); //van a ir todos los marker
        id_graffiti = new ArrayList<String>();
        nombre_graffiti = new ArrayList<String>();
        descripcion_graffiti = new ArrayList<String>();
        link_graffiti = new ArrayList<String>();

        //nos colacamos a la escucha del gps y en caso de obtener y detectar un cambio de coordenadas nos posicionamos en el lugar
        //y se realiza la busqueda de los lugares cercanos
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
                        latitud = location.getLatitude();
                        longitud = location.getLongitude();
                        SystemClock.sleep(1000);

                        if (realizarZoom == false && latitud != 0 && longitud != 0) {
                            realizarZoom = true;
                            //Se mueve el mapa a donde se encuentre nuestra ubicacion indicada por el gps
                            CameraUpdate cam = CameraUpdateFactory.newLatLng(new LatLng(latitud, longitud));
                            map.animateCamera(cam);//realizar ajuste de pantalla

                            map.setOnMyLocationChangeListener(null); //se termina el proceso de escucha del gps tan solo se realiza una vez

                            Toast.makeText(getActivity(), "Buscando graffitis cercanos", Toast.LENGTH_SHORT).show();

                            ///////////////////////////////
                            String ruta = "http://192.168.42.146:8080/graffcity/graffiti/GPS?latitud="+String.valueOf(latitud)+"&longitud="+String.valueOf(longitud);
                            new AgregarLugaresMapa().execute(internet,ruta);
                            ///////////////////////////////
                            //realizar llamado para marker
                            ///////////////////////////////

                            ///////////////////////////////
                            iniciarListenerMaker();
                            ////////////////////////////////
                        }   //escuchar los marker
                            ////////////////////////////////

                    }
                });
            } else {
                //******  HERE's the PROBLEM  ********
                Toast.makeText(getActivity(), "Debe activar GPS", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(getActivity(), "No tiene conexión a internet", Toast.LENGTH_LONG).show();
        }

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

    public void iniciarListenerMaker() {
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent ver_graffiti = new Intent(context, VerGraffiti.class);
                for (int i = 0; i < markerList.size(); i++) {
                    if (markerList.get(i).equals(marker)) {
                        Bundle bolsa = new Bundle();
                        bolsa.putString("id", id_graffiti.get(i));
                        bolsa.putString("link", link_graffiti.get(i));
                        bolsa.putString("nombre", nombre_graffiti.get(i));
                        bolsa.putString("descripcion", descripcion_graffiti.get(i));
                        ver_graffiti.putExtras(bolsa);
                        break;
                    }
                }
                startActivity(ver_graffiti);

            }

        });
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Clase que es la encargada de realizar la consulta al servicio django
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private class AgregarLugaresMapa extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            if(urls[0] == "si") {

                Consultas c = new Consultas();
                String r = c.graffitisCercanos(urls[1]);

                if(r.length()<5) {
                    return "no";
                }else{
                    return r;
                }
            }else{
                return "no";
            }
        }
        /////////////////////////////////////////////////////////////////////////////////////////////////////
        //Una vez terminada la consulta nos devolvera un string el cual será utilizado como arreglo para
        //marcar los lugares cercanos en el mapa de google maps
        /////////////////////////////////////////////////////////////////////////////////////////////////////
        @Override
        protected void onPostExecute(String result) {

            JsonHandler js = new JsonHandler();

            String[][] arreglo = js.getGraffitisCercanos(result);

            //se recorre el arreglo y dependiendo de a la categoria que pertenece el lugar se ´pintara de ciertos colores el marker.
            if(arreglo[0][0] != "no") {

                for (int i = 0; i < arreglo.length; i++) {
                    LatLng PERTH = new LatLng(Double.parseDouble(arreglo[i][5]), Double.parseDouble(arreglo[i][6]));
                    Marker m = map.addMarker(new MarkerOptions().position(PERTH).draggable(true).title(arreglo[i][4]).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    markerList.add(m);
                    id_graffiti.add(arreglo[i][2]);
                    nombre_graffiti.add(arreglo[i][4]);
                    descripcion_graffiti.add(arreglo[i][1]);
                    link_graffiti.add(arreglo[i][3]);
                }
            }
        }
    }
}
