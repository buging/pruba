package com.example.buging.graffcity;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Buging on 27-12-2015.
 */
public class Consultas {

    public String session(String ruta){
        try {
            URL url = new URL(ruta);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(10000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();
            return new Scanner(connection.getInputStream(), "UTF-8").useDelimiter("\\A").next();
        }catch (MalformedURLException e) {
            Log.e("ERROR", this.getClass().toString() + " " + e.toString());
        } catch (ProtocolException e) {
            Log.e("ERROR", this.getClass().toString() + " " + e.toString());
        } catch (IOException e) {
            Log.e("ERROR", this.getClass().toString() + " " + e.toString());
        }

        return "";
    }

    public String graffitisCercanos(String ruta){
        try {
            URL url = new URL(ruta);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(10000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();
            return new Scanner(connection.getInputStream(), "UTF-8").useDelimiter("\\A").next();
        } catch (MalformedURLException e) {
            Log.e("ERROR", this.getClass().toString() + " " + e.toString());
        } catch (ProtocolException e) {
            Log.e("ERROR", this.getClass().toString() + " " + e.toString());
        } catch (IOException e) {
            Log.e("ERROR", this.getClass().toString() + " " + e.toString());
        }
        return "";
    }

    public Boolean calificar(Boolean tiene_cal, String id_usuario, String id_graffiti, String calificacion){
        if(tiene_cal == true) {
            try {
                String ruta = "http://192.168.42.146:8080/graffcity/calificacion/edit?user="+id_usuario+"&graf="+id_graffiti+"&nota="+calificacion;
                URL url = new URL(ruta);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(10000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();
                InputStream is = connection.getInputStream();
                return true;
            } catch (MalformedURLException e) {
                Log.e("ERROR", this.getClass().toString() + " " + e.toString());
            } catch (ProtocolException e) {
                Log.e("ERROR", this.getClass().toString() + " " + e.toString());
            } catch (IOException e) {
                Log.e("ERROR", this.getClass().toString() + " " + e.toString());
            }
            return false;
        }else{
            try {
                /*conexion*/
                String ruta = "http://192.168.42.146:8080/graffcity/calificacion";
                URL url = new URL(ruta);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(10000);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                /*string que contiene el llamado al servidor*/
                String query = "{\"graffitiId\":"+id_graffiti+",\"puntuacion\":"+calificacion+",\"usuarioId\":"+id_usuario+"}";
                DataOutputStream os = new DataOutputStream(connection.getOutputStream());

                os.writeBytes(query);
                os.flush();
                os.close();

                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                rd.close();
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    public int tieneCalificacion(String id_graffiti, String id_usuario){
        try {
            String ruta = "http://192.168.42.146:8080/graffcity/calificacion/existe?user="+id_usuario+"&graf="+id_graffiti;
            URL url = new URL(ruta);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(10000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();
            ruta = new Scanner(connection.getInputStream(), "UTF-8").useDelimiter("\\A").next();
            int calificacion = -1;
            if(ruta=="[]"){
                return calificacion;
            }
            JSONArray ja = new JSONArray(ruta);
            for (int i = 0; i < 1; i++) {
                JSONObject row = ja.getJSONObject(i);
                calificacion = row.optInt("puntuacion");
            }
            return calificacion;
        } catch (MalformedURLException e) {
            Log.e("ERROR", this.getClass().toString() + " " + e.toString());
        } catch (ProtocolException e) {
            Log.e("ERROR", this.getClass().toString() + " " + e.toString());
        } catch (IOException e) {
            Log.e("ERROR", this.getClass().toString() + " " + e.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public Boolean validarNickName(String nickName){
        try {
            String ruta = "http://192.168.42.146:8080/graffcity/usuarios/ex.us/"+nickName;
            URL url = new URL(ruta);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(10000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();
            ruta = new Scanner(connection.getInputStream(), "UTF-8").useDelimiter("\\A").next();
            if(ruta.length()>4){
                return true;
            }else{
                return false;
            }
        } catch (MalformedURLException e) {
            Log.e("ERROR", this.getClass().toString() + " " + e.toString());
        } catch (ProtocolException e) {
            Log.e("ERROR", this.getClass().toString() + " " + e.toString());
        } catch (IOException e) {
            Log.e("ERROR", this.getClass().toString() + " " + e.toString());
        }
        return true;
    }

    public Boolean validarCorreo(String correo){

        boolean salida = true;

        try {
            if(correo.contains("@") == false){
                return true;
            }
            String ruta = "http://192.168.42.146:8080/graffcity/usuarios/existe";
            URL url = new URL(ruta);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(10000);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

                /*string que contiene el llamado al servidor*/
            String query = "{\"correo\": \""+correo+"\"}";

            DataOutputStream os = new DataOutputStream(connection.getOutputStream());

            os.writeBytes(query);
            os.flush();
            os.close();

            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));

            String line;
            StringBuffer response = new StringBuffer();
            int cont =0;
            while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
                cont++;
            }
            rd.close();
            line = response.toString();

            if(line.equals("[]\r")){
                salida = false;
                return false;
            }else{
                salida = true;
                return true;
            }
        } catch (Exception e) {
            if(salida == true){
                return true;
            }else{
                return false;
            }
        }
    }


    public String publicarGraffitis(String u,int autorId, int comunaId,String nombre ,String descripcion, double latitud, double longitud, String link, int numCom, boolean revision){
        try {
                /*conexion*/
            URL url = new URL(u);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(10000);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

                /*string que contiene el llamado al servidor*/
            String query = "{\"autorId\": " + Integer.toString(autorId) + ", \"comunaId\": " + Integer.toString(comunaId) +", \"descripcionGraf\": \""+descripcion+"\",\"latitud\":"+Double.toString(latitud)+",\"linkFoto\":\""+link +"\",\"longitud\":"+Double.toString(longitud)+",\"nombreGraffiti\":\""+nombre+"\", \"numeroCompartidas\":"+Integer.toString(numCom)+",\"revision\":"+Boolean.toString(revision)+"}";


            DataOutputStream os = new DataOutputStream(connection.getOutputStream());

            os.writeBytes(query);
            os.flush();
            os.close();

            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            rd.close();
            return "Exito al publicar graffity";
        } catch (Exception e) {
            return "Error al publicar graffity";
        }
    }

    public Boolean registrar(String nombre, String apellido, String nickName, String correo,String password){
        try {
                /*conexion*/
            String ruta = "http://192.168.42.146:8080/graffcity/usuarios";
            URL url = new URL(ruta);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(10000);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

                /*string que contiene el llamado al servidor*/
            String query = "{\"apellido\":\""+apellido+"\",\"contrasena\":\""+password+"\",\"correo\":\""+correo+"\",\"nickname\":\""+nickName+"\",\"nombre\":\""+nombre+"\"}";

            DataOutputStream os = new DataOutputStream(connection.getOutputStream());

            os.writeBytes(query);
            os.flush();
            os.close();

            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            rd.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
