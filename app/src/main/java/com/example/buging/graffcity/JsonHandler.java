package com.example.buging.graffcity;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

/**
 * @author: Jefferson Morales De la Parra
 * Clase que se utiliza para manipular objetos JSON
 */
public class JsonHandler {

    public String[] getSession(String usuario) {
        if(usuario != "no") {
            try {
                JSONArray ja = new JSONArray(usuario);
                String[] result = new String[5];
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject row = ja.getJSONObject(i);
                    result[0] = row.getString("usuarioId");
                    result[1] = row.getString("nombre");
                    result[2] = row.getString("apellido");
                    result[3] = row.getString("nickname");
                    result[4] = row.getString("correo");
                }
                return result;
            } catch (JSONException e) {
                Log.e("ERROR", this.getClass().toString() + " " + e.toString());
            }
        }
        String[] result = new String[1];
        result[0] = "no";
        return result;
    }// getActors(String actors)

    public String[][] getGraffitisCercanos(String graffitis) {
        if (graffitis != "") {
            try {
                int n;
                JSONArray ja = new JSONArray(graffitis);
                String[][] result = new String[ja.length()][7];
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject row = ja.getJSONObject(i);
                    result[i][0] = String.valueOf(row.optInt("AutorId"));
                    result[i][1] = row.optString("descripcionGraf");
                    result[i][2] = String.valueOf(row.optInt("graffitiId"));
                    result[i][3] = row.optString("linkFoto");
                    result[i][4] = row.optString("nombreGraffiti");
                    result[i][5] = String.valueOf(row.optDouble("latitud"));
                    result[i][6] = String.valueOf(row.optDouble("longitud"));
                }
                return result;
            } catch (JSONException e) {
                Log.e("ERROR", this.getClass().toString() + " " + e.toString());
            }
        }
        String[][] result = new String[1][1];
        result[0][0] = "no";
        return result;
    }

}// JsonHandler