package com.iesfernandowirtz.clothesvault;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.iesfernandowirtz.clothesvault.Modelo.Usuario;
import com.iesfernandowirtz.clothesvault.Utils.Apis;
import com.iesfernandowirtz.clothesvault.Utils.ServicioUsuario;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {
    Spinner spinnerIdiomas;
    public static final String[] idiomas = {"", "Español", "Gallego"};
    public static final int[] imagenes = {R.drawable.icon, R.drawable.espanhol, R.drawable.gallego};
    ServicioUsuario servicioUsuario;
    EditText txtEmail;
    EditText txtContrasenha;

    public String nombreUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        Button btInciarSesion = findViewById(R.id.btInciarSesion);
        Button btRegistrarse = findViewById(R.id.btRegistrarse);
        txtEmail = findViewById(R.id.loginEtEmail);
        txtContrasenha = findViewById(R.id.loginEtContrasenha);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;


        });


        spinnerIdiomas = findViewById(R.id.spinnerIdiomas);
        SpinnerIdiomas adapter = new SpinnerIdiomas(this, R.layout.idioma_row, idiomas, imagenes);
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


                login(txtEmail.getText().toString().toLowerCase(), txtContrasenha.getText().toString());



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


    public void limpiarCampos() { // Limpiar todos los campos de la pantalla
        txtEmail.getText().clear();
        txtContrasenha.getText().clear();
    }

    private void login(String email, String contrasenha) {
        servicioUsuario = Apis.getServicioUsuario();

        // Construir la URL completa con el correo electrónico proporcionada
        Call<List<Usuario>> call = servicioUsuario.getUsuarioXEmail(email);

        call.enqueue(new Callback<List<Usuario>>() {
            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Usuario> usuarios = response.body();
                    if (!usuarios.isEmpty()) {
                        Usuario usuario = usuarios.get(0);
                        nombreUsuario = usuario.getNombre();
                        String contrasenhaAlmacenada = usuario.getContrasenha();
                        if (verificarContrasenha(contrasenha, contrasenhaAlmacenada)) {
                            limpiarCampos();
                            Toast.makeText(Login.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();

                            // Crear un Intent para MainActivity y pasar el nombreUsuario
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            intent.putExtra("nombreUsuario", nombreUsuario);
                            startActivity(intent);
                        } else {
                            Toast.makeText(Login.this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(Login.this, "Usuario Incorrecto", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Login.this, "Error al obtener información del usuario", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable throwable) {
                Log.e("Error", throwable.toString());
                Toast.makeText(Login.this, "Error de red", Toast.LENGTH_SHORT).show();
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