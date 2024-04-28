package com.iesfernandowirtz.clothesvault;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class Login extends AppCompatActivity {
    Spinner spinnerIdiomas;
    public static final String[] idiomas = {"Seleccionar Idioma", "Español", "Gallego"};
    public static final int[] imagenes = {R.drawable.icon, R.drawable.espanhol, R.drawable.gallego};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        Button btInciarSesion = findViewById(R.id.btInciarSesion);
        Button btRegistrarse = findViewById(R.id.btRegistrarse);
        EditText etEmail= findViewById(R.id.loginEtEmail);
        EditText etContrasenha = findViewById(R.id.loginEtContrasenha);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;


        });


        spinnerIdiomas = findViewById(R.id.spinnerIdiomas);
        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(this, R.layout.row, idiomas, imagenes);
        spinnerIdiomas.setAdapter(adapter);
        spinnerIdiomas.setSelection(0);
        spinnerIdiomas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String idiomaSeleccionado = parent.getItemAtPosition(position).toString();

                if (idiomaSeleccionado.equals("Español")) {
                    setLocal(Login.this, "es");
                    finish();
                    startActivity(getIntent());

                } else if (idiomaSeleccionado.equals("Gallego")) {
                    setLocal(Login.this, "gl");
                    finish();
                    startActivity(getIntent());

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        btInciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, MainActivity.class);

                // Iniciar la MainActivity
                startActivity(intent);
            }
        });

        btRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Registro.class);

                // Iniciar la actividad de Registro
                startActivity(intent);
            }
        });


    }

    public void setLocal(Activity activity, String codIdioma) {
        Locale locale = new Locale(codIdioma);
        locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());

    }

    private boolean verificarContrasenha(String contrasenhaIntroducida, String contrasenhaAlmacenada) {
        String contrasenhaCifrada = Registro.cifrarContrasenha(contrasenhaIntroducida);
        return contrasenhaCifrada != null && contrasenhaCifrada.equals(contrasenhaAlmacenada);
    }
}