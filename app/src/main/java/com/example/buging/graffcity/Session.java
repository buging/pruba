package com.example.buging.graffcity;

import android.app.Application;

/**
 * Created by Buging on 26-12-2015.
 */
public class Session extends Application{

    private static int id;
    private static String usuario;

    public Session() {
    }

    public Session(int id_new, String usuario_new) {
        id = id_new;
        usuario = usuario_new;
    }

    public int getId() {
        return id;
    }

    public String getUsuario() {
        return usuario;
    }
}
