package com.iesfernandowirtz.clothesvault;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.iesfernandowirtz.clothesvault.Utils.Apis;

import java.util.Locale;

public class Opciones extends ActividadBase {
    Spinner spinnerIdiomas;
    public static final String[] idiomas = {"", "Español", "Gallego"};
    public static final int[] imagenes = {R.drawable.icon, R.drawable.espanhol, R.drawable.gallego};

    ImageView btAtras;
    EditText etIntIP;
    Button btCambiarIp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opciones);
        btAtras = (ImageView) findViewById(R.id.btAtras);
        etIntIP = findViewById(R.id.etIntIP);
        btCambiarIp = findViewById(R.id.btCambiarIp);

        spinnerIdiomas = findViewById(R.id.spinnerIdiomas);
        SpinnerIdiomas adapter = new SpinnerIdiomas(this, R.layout.idioma_row, idiomas, imagenes);
        spinnerIdiomas.setAdapter(adapter);
        spinnerIdiomas.setSelection(0);
        spinnerIdiomas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String idiomaSeleccionado = parent.getItemAtPosition(position).toString();
                String codIdioma = "";

                if (idiomaSeleccionado.equals("Español")) {
                    codIdioma = "es";
                } else if (idiomaSeleccionado.equals("Gallego")) {
                    codIdioma = "gl";
                }

                if (!codIdioma.isEmpty()) {
                    getSharedPreferences("AppPreferences", MODE_PRIVATE)
                            .edit()
                            .putString("language", codIdioma)
                            .apply();
                    restartApp();
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Opciones.this, Login.class);
                startActivity(intent);
            }
        });

        btCambiarIp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarIp(v);
            }
        });



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorFondo));
        }
    }
    public void cambiarIp(View view) {
        String nuevaIP = etIntIP.getText().toString().trim();
        if (!nuevaIP.isEmpty()) {
            // Obtener la posición del inicio de la IP en la URL
            int inicioIP = Apis.URL_001.indexOf("://") + 3;
            // Obtener la posición del final de la IP en la URL
            int finIP = Apis.URL_001.indexOf(":", inicioIP);
            if (finIP == -1) { // Si no hay un ":" después de la IP, consideramos el final de la URL
                finIP = Apis.URL_001.indexOf("/", inicioIP);
                if (finIP == -1) { // Si no hay un "/" después de la IP, consideramos el final de la cadena
                    finIP = Apis.URL_001.length();
                }
            }
            // Reemplazar la IP antigua con la nueva en la URL
            String nuevaURL = Apis.URL_001.substring(0, inicioIP) + nuevaIP + Apis.URL_001.substring(finIP);
            Apis.URL_001 = nuevaURL;
            Toast.makeText(this, "IP cambiada a: " + nuevaIP, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Introduce una IP válida", Toast.LENGTH_SHORT).show();
        }
    }
    private void restartApp() {
        Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }
}
