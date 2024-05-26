package com.iesfernandowirtz.clothesvault;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.iesfernandowirtz.clothesvault.Modelo.Usuario;
import com.iesfernandowirtz.clothesvault.Utils.Apis;
import com.iesfernandowirtz.clothesvault.Utils.ServicioUsuario;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends ActividadBase {

    ServicioUsuario servicioUsuario;
    EditText txtEmail;
    EditText txtContrasenha;
    CheckBox checkboxRecordarEmail;

    public String nombreUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        Button btInciarSesion = findViewById(R.id.btInciarSesion);
        Button btRegistrarse = findViewById(R.id.btRegistrarse);
        txtEmail = findViewById(R.id.loginEtEmail);
        txtContrasenha = findViewById(R.id.loginEtContrasenha);
        ImageView opciones = findViewById(R.id.loginOpciones);
        checkboxRecordarEmail = findViewById(R.id.checkboxRecordarEmail);

        manejarCheckBox(checkboxRecordarEmail, txtEmail);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        }

        View container = findViewById(R.id.scrollViewLogin);
        container.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Oculta el teclado cuando se toca fuera de los campos de texto
                Utilidades.ocultarTeclado(Login.this, v);
                return false;
            }
        });

        opciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Opciones.class);
                // Iniciar la actividad de Registro
                startActivity(intent);
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

                            if(!checkboxRecordarEmail.isChecked()){
                                limpiarCampos();
                            }else{
                                txtContrasenha.getText().clear();
                            }

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
                    Toast.makeText(Login.this, "Error al insertar los datos de usuario", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable throwable) {
                Log.e("Error", throwable.toString());
                Toast.makeText(Login.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private boolean verificarContrasenha(String contrasenhaIntroducida, String contrasenhaAlmacenada) {
        String contrasenhaCifrada = Registro.cifrarContrasenha(contrasenhaIntroducida);
        return contrasenhaCifrada != null && contrasenhaCifrada.equals(contrasenhaAlmacenada);
    }
    private static final String NOM_PREFS = "prefs";
    private static final String EMAIL_KEY = "email";

    public static void guardarEmail(Context context, String email) {
        SharedPreferences.Editor editor = context.getSharedPreferences(NOM_PREFS, Context.MODE_PRIVATE).edit();
        editor.putString(EMAIL_KEY, email);
        editor.apply();
    }

    public static String obtenerEmail(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(NOM_PREFS, Context.MODE_PRIVATE);
        return prefs.getString(EMAIL_KEY, "");
    }

    public static void manejarCheckBox(CheckBox checkBox, EditText editText) {
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                String email = editText.getText().toString();
                if (Utilidades.validarFormatoCorreo(email)) {
                    guardarEmail(editText.getContext(), email);
                } else {
                    checkBox.setChecked(false); // Desmarcar el checkbox si el email no es válido
                    Toast.makeText(editText.getContext(), "Por favor, introduce un email válido", Toast.LENGTH_SHORT).show();
                }
            } else {
                guardarEmail(editText.getContext(), "");
            }
        });

        String emailGuardado = obtenerEmail(editText.getContext());
        editText.setText(emailGuardado);
        checkBox.setChecked(!emailGuardado.isEmpty());
    }


}