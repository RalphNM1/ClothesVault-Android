package com.iesfernandowirtz.clothesvault;

import static com.iesfernandowirtz.clothesvault.Utilidades.mostrarToastError;
import static com.iesfernandowirtz.clothesvault.Utilidades.mostrarToastSuccess;

import android.content.Intent;
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

import androidx.core.content.ContextCompat;

import com.iesfernandowirtz.clothesvault.utils.Apis;

// Actividad que gestiona las opciones de la aplicación
public class Opciones extends ActividadBase {
    Spinner spinnerIdiomas;
    public static final String[] idiomas = {"", "Español", "Gallego"};
    public static final int[] imagenes = {R.drawable.icon, R.drawable.espanhol, R.drawable.gallego};

    ImageView btAtras;
    EditText etIntIP;
    Button btCambiarIp;
    DatabaseOperaciones dbOps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opciones);
        dbOps = new DatabaseOperaciones(this);
        Apis.setDireccionIP(this);

        // Inicialización de elementos de la interfaz de usuario
        btAtras = findViewById(R.id.btAtras);
        etIntIP = findViewById(R.id.etIntIP);
        btCambiarIp = findViewById(R.id.btCambiarIp);

        // Comprobación de la existencia de una dirección IP guardada
        String ipActual = dbOps.obtenerDireccionIP();
        if (ipActual != null && !ipActual.isEmpty()) {
            etIntIP.setText(ipActual);
        } else {
            // Si no hay ninguna dirección IP guardada, agregar la dirección IP por defecto
            dbOps.guardarDireccionIP("192.168.1.133");
            etIntIP.setText("192.168.1.133");
        }

        // Configuración del Spinner de idiomas
        spinnerIdiomas = findViewById(R.id.spinnerIdiomas);
        SpinnerIdiomas adapter = new SpinnerIdiomas(this, R.layout.idioma_row, idiomas, imagenes);
        spinnerIdiomas.setAdapter(adapter);
        spinnerIdiomas.setSelection(0);
        spinnerIdiomas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String idiomaSeleccionado = parent.getItemAtPosition(position).toString();
                String codIdioma = "";

                // Asignación del código de idioma según la selección del usuario
                if (idiomaSeleccionado.equals("Español")) {
                    codIdioma = "es";
                } else if (idiomaSeleccionado.equals("Gallego")) {
                    codIdioma = "gl";
                }

                // Guardar el idioma seleccionado en las preferencias compartidas y reiniciar la aplicación
                if (!codIdioma.isEmpty()) {
                    getSharedPreferences("AppPreferences", MODE_PRIVATE)
                            .edit()
                            .putString("language", codIdioma)
                            .apply();
                    reiniciarApp();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Listener para el botón de retroceso
        btAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Listener para el botón de cambiar IP
        btCambiarIp.setOnClickListener(v -> cambiarIp());

        // Configuración del color de la barra de estado
        Utilidades.cambiarColorBarraDeEstado(getApplicationContext(),getWindow(),R.color.colorFondo);

    }

    // Método para cambiar la dirección IP
    public void cambiarIp() {
        String nuevaIP = etIntIP.getText().toString().trim();
        if (!nuevaIP.isEmpty()) {
            dbOps.guardarDireccionIP(nuevaIP);
            Apis.setDireccionIP(this); // Actualiza la URL en la clase Apis
            mostrarToastSuccess(getApplicationContext(), "IP cambiada a: " + nuevaIP);
        } else {
            mostrarToastError(getApplicationContext(), "Introduce una IP válida");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbOps.cerrar();
    }

    // Método para reiniciar la aplicación
    private void reiniciarApp() {
        Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }
}
