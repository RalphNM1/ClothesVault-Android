package com.iesfernandowirtz.clothesvault;

import static com.iesfernandowirtz.clothesvault.Utilidades.mostrarToastError;
import static com.iesfernandowirtz.clothesvault.Utilidades.mostrarToastSuccess;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import com.iesfernandowirtz.clothesvault.modelo.modeloUsuario;
import com.iesfernandowirtz.clothesvault.utils.Apis;
import com.iesfernandowirtz.clothesvault.utils.ServicioUsuario;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * La clase Login maneja la lógica de la pantalla de inicio de sesión.
 */
public class Login extends ActividadBase {

    ServicioUsuario servicioUsuario; // Servicio para realizar llamadas a la API de usuarios
    EditText txtEmail; // Campo de texto para el email
    EditText txtContrasenha; // Campo de texto para la contraseña
    CheckBox checkboxRecordarEmail; // CheckBox para recordar el email
    Button btInciarSesion; // Botón para iniciar sesión

    public String nombreUsuario; // Nombre del usuario autenticado
    public String emailUsuario; // Email del usuario autenticado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Apis.setDireccionIP(this);
        setContentView(R.layout.activity_login);

        // Inicializar los componentes de la interfaz
        btInciarSesion = findViewById(R.id.btInciarSesion);
        Button btRegistrarse = findViewById(R.id.btRegistrarse);
        txtEmail = findViewById(R.id.loginEtEmail);
        txtContrasenha = findViewById(R.id.loginEtContrasenha);
        ImageView opciones = findViewById(R.id.loginOpciones);
        checkboxRecordarEmail = findViewById(R.id.checkboxRecordarEmail);

        // Manejar la lógica del CheckBox
        manejarCheckBox(checkboxRecordarEmail, txtEmail);

        // Cambiar el color de la barra de estado si la versión de Android es Lollipop o superior
        Utilidades.cambiarColorBarraDeEstado(getApplicationContext(),getWindow(),R.color.black);

        // Configurar la lógica para ocultar el teclado al tocar fuera de los campos de texto
        View container = findViewById(R.id.scrollViewLogin);
        container.setOnTouchListener((v, event) -> {
            Utilidades.ocultarTeclado(Login.this, v);
            return false;
        });

        // Configurar el botón de opciones
        opciones.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, Opciones.class);
            txtContrasenha.getText().clear();
            startActivity(intent);
        });

        // Configurar el botón de iniciar sesión
        btInciarSesion.setOnClickListener(v -> {
            btInciarSesion.setEnabled(false); // Deshabilitar el botón para evitar múltiples clics
            login(txtEmail.getText().toString().toLowerCase(), txtContrasenha.getText().toString());
            Utilidades.ocultarTeclado(Login.this, v);
        });

        // Configurar el botón de registrarse
        btRegistrarse.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, Registro.class);
            txtContrasenha.getText().clear();
            if (!checkboxRecordarEmail.isChecked()) {
                txtEmail.setText("");
            }
            startActivity(intent);
        });
    }

    /**
     * Limpiar todos los campos de la pantalla de inicio de sesión.
     */
    public void limpiarCampos() {
        txtEmail.getText().clear();
        txtContrasenha.getText().clear();
    }

    /**
     * Iniciar sesión con el email y la contraseña proporcionados.
     * @param email El email del usuario.
     * @param contrasenha La contraseña del usuario.
     */
    private void login(String email, String contrasenha) {
        servicioUsuario = Apis.getServicioUsuario(this);

        // Verificar si el correo electrónico existe
        Call<List<modeloUsuario>> call = servicioUsuario.getUsuarioXEmail(email);
        call.enqueue(new Callback<List<modeloUsuario>>() {
            @Override
            public void onResponse(Call<List<modeloUsuario>> call, Response<List<modeloUsuario>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<modeloUsuario> usuarios = response.body();
                    if (!usuarios.isEmpty()) {
                        modeloUsuario usuario = usuarios.get(0);
                        nombreUsuario = usuario.getNombre();
                        emailUsuario = usuario.getEmail();
                        String contrasenhaAlmacenada = usuario.getContrasenha();
                        if (verificarContrasenha(contrasenha, contrasenhaAlmacenada)) {
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            intent.putExtra("nombreUsuario", nombreUsuario);
                            intent.putExtra("email", emailUsuario);
                            if (!checkboxRecordarEmail.isChecked()) {
                                limpiarCampos();
                            } else {
                                txtContrasenha.getText().clear();
                            }
                            mostrarToastSuccess(getApplicationContext(), "Iniciado Sesión...");
                            cargarMainActivity(intent);
                        } else {
                            mostrarToastError(getApplicationContext(), "Contraseña incorrecta");
                            btInciarSesion.setEnabled(true); // Rehabilitar el botón si hay un error
                        }
                    } else {
                        mostrarToastError(getApplicationContext(), "Usuario incorrecto");
                        btInciarSesion.setEnabled(true); // Rehabilitar el botón si hay un error
                    }
                } else {
                    mostrarToastError(getApplicationContext(), "Email o contraseña incorrectos");
                    btInciarSesion.setEnabled(true); // Rehabilitar el botón si hay un error
                }
            }

            @Override
            public void onFailure(Call<List<modeloUsuario>> call, Throwable throwable) {
                Log.e("Error", throwable.toString());
                mostrarToastError(getApplicationContext(), "Error de red");
                btInciarSesion.setEnabled(true); // Rehabilitar el botón si hay un error
            }
        });
    }

    /**
     * Cargar la actividad principal después de iniciar sesión.
     * @param intent El intent para iniciar la actividad principal.
     */
    public void cargarMainActivity(Intent intent) {
        startActivity(intent);
        btInciarSesion.setEnabled(true); // Rehabilitar el botón después de la transición
    }

    /**
     * Verificar si la contraseña introducida coincide con la almacenada.
     * @param contrasenhaIntroducida La contraseña introducida por el usuario.
     * @param contrasenhaAlmacenada La contraseña almacenada en la base de datos.
     * @return true si las contraseñas coinciden, false en caso contrario.
     */
    private boolean verificarContrasenha(String contrasenhaIntroducida, String contrasenhaAlmacenada) {
        String contrasenhaCifrada = Registro.cifrarContrasenha(contrasenhaIntroducida);
        return contrasenhaCifrada != null && contrasenhaCifrada.equals(contrasenhaAlmacenada);
    }

    private static final String NOM_PREFS = "prefs"; // Nombre del archivo de preferencias
    private static final String EMAIL_KEY = "email"; // Clave para el email en las preferencias

    /**
     * Guardar el email en las preferencias compartidas.
     * @param context El contexto de la aplicación.
     * @param email El email a guardar.
     */
    public static void guardarEmail(Context context, String email) {
        SharedPreferences.Editor editor = context.getSharedPreferences(NOM_PREFS, Context.MODE_PRIVATE).edit();
        editor.putString(EMAIL_KEY, email);
        editor.apply();
    }

    /**
     * Obtener el email guardado en las preferencias compartidas.
     * @param context El contexto de la aplicación.
     * @return El email guardado.
     */
    public static String obtenerEmail(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(NOM_PREFS, Context.MODE_PRIVATE);
        return prefs.getString(EMAIL_KEY, "");
    }

    /**
     * Manejar la lógica del CheckBox para recordar el email.
     * @param checkBox El CheckBox para recordar el email.
     * @param editText El campo de texto del email.
     */
    public void manejarCheckBox(CheckBox checkBox, EditText editText) {
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                String email = editText.getText().toString();
                if (Utilidades.validarFormatoCorreo(email)) {
                    guardarEmail(editText.getContext(), email);
                } else {
                    checkBox.setChecked(false); // Desmarcar el checkbox si el email no es válido
                    mostrarToastError(getApplicationContext(), "Por favor, introduce un email válido");
                }
            } else {
                guardarEmail(editText.getContext(), ""); // Limpiar el correo guardado si se desmarca la casilla
            }
        });

        // Agregar un TextWatcher para detectar cambios en el texto del EditText
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Verificar si el CheckBox está marcado y el nuevo texto del EditText es un correo electrónico válido
                if (checkboxRecordarEmail.isChecked() && Utilidades.validarFormatoCorreo(s.toString())) {
                    guardarEmail(editText.getContext(), s.toString());
                }
            }
        });

        // Si hay un correo guardado, establecerlo en el EditText y marcar la casilla
        String emailGuardado = obtenerEmail(editText.getContext());
        editText.setText(emailGuardado);
        checkBox.setChecked(!emailGuardado.isEmpty());
    }
}
