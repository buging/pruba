package com.example.buging.graffcity;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cloudinary.utils.ObjectUtils;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class RegistroActivity extends AppCompatActivity{

    private AutoCompleteTextView nombre;
    private AutoCompleteTextView apellido;
    private AutoCompleteTextView nickName;
    private AutoCompleteTextView correo;
    private EditText password;
    private EditText con_password;
    private Button bt_register;
    private View mProgressView;
    private String nick;
    private String email;
    private String ape;
    private String nom;
    private String pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //para que se active la flecha de regreso

        nombre = (AutoCompleteTextView) findViewById(R.id.register_nombre);
        apellido = (AutoCompleteTextView) findViewById(R.id.register_apellido);
        nickName = (AutoCompleteTextView) findViewById(R.id.register_nickName);
        correo = (AutoCompleteTextView) findViewById(R.id.register_email);

        password = (EditText) findViewById(R.id.register_password);
        con_password = (EditText) findViewById(R.id.register_con_password);

        bt_register = (Button) findViewById(R.id.register_button);

        mProgressView = findViewById(R.id.login_progress);

        bt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nombre.getText().toString().length() < 5) {
                    Toast.makeText(getApplicationContext(), "Ingrese un nombre valido", Toast.LENGTH_LONG).show();
                    nombre.requestFocus();
                    return;
                } else if (apellido.getText().toString().length() < 5) {
                    Toast.makeText(getApplicationContext(), "Ingrese un apellido valido", Toast.LENGTH_LONG).show();
                    apellido.requestFocus();
                    return;
                } else if (nickName.getText().toString().length() < 5) {
                    Toast.makeText(getApplicationContext(), "Ingrese un nombre artistico valido", Toast.LENGTH_LONG).show();
                    nickName.requestFocus();
                    return;
                } else if (correo.getText().toString().length() < 5) {
                    Toast.makeText(getApplicationContext(), "Ingrese un correo valido", Toast.LENGTH_LONG).show();
                    correo.requestFocus();
                    return;
                } else if (password.getText().toString().length() < 8) {
                    Toast.makeText(getApplicationContext(), "Contraseña es muy corta minimo 8 caracteres", Toast.LENGTH_LONG).show();
                    password.requestFocus();
                    return;
                } else if (!con_password.getText().toString().equals(password.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Contraseñas no son iguales", Toast.LENGTH_LONG).show();
                    password.requestFocus();
                    con_password.requestFocus();
                    return;
                }

                SystemUtilities su = new SystemUtilities(getApplicationContext().getApplicationContext());
                if (su.isNetworkAvailable()) {
                    nick = nickName.getText().toString();
                    email = correo.getText().toString();
                    nom = nombre.getText().toString();
                    ape = apellido.getText().toString();
                    pass = password.getText().toString();
                    new ValidarDatos().execute();
                } else {
                    Toast.makeText(getApplicationContext(), "No tiene conexión a internet", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    //para que vuleva hacia atras
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                Intent login = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(login);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class ValidarDatos extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... urls) {
            Consultas c = new Consultas();
            boolean r = c.validarNickName(nick);
            if(r == true){
                return "nick";
            }
            r = c.validarCorreo(email);
            if(r == true){
                return "email";
            }

            r = c.registrar(nom,ape,nick,email,pass);
            if(r==true){
                return "si";
            }
            return "no";
        }

        @Override
        protected void onPostExecute(String result) {

            if(result == "si"){
                Toast.makeText(getApplicationContext(), "Usuario creado con exito", Toast.LENGTH_LONG).show();
                Intent log = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(log);
                finish();
            }else if(result == "no"){
                Toast.makeText(getApplicationContext(), "Error al crear usuario", Toast.LENGTH_LONG).show();
            }else if(result == "nick"){
                Toast.makeText(getApplicationContext(), "Nombre artistico ya existente", Toast.LENGTH_LONG).show();
                nickName.requestFocus();
            }else if(result == "email"){
                Toast.makeText(getApplicationContext(), "Correo ya existente", Toast.LENGTH_LONG).show();
                correo.requestFocus();
            }


        }


    }
}
