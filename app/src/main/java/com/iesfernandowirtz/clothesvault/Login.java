package com.iesfernandowirtz.clothesvault;

import static com.iesfernandowirtz.clothesvault.Utilidades.mostrarToastError;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import com.iesfernandowirtz.clothesvault.modelo.modeloUsuario;
import com.iesfernandowirtz.clothesvault.modelo.modeloDetallePedido;
import com.iesfernandowirtz.clothesvault.utils.Apis;
import com.iesfernandowirtz.clothesvault.utils.ServicioUsuario;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends ActividadBase {

    ServicioUsuario servicioUsuario;
    EditText txtEmail;
    EditText txtContrasenha;
    CheckBox checkboxRecordarEmail;
    Button btInciarSesion;

    public String nombreUsuario;
    CargandoAlert cargandoAlert = new CargandoAlert(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Apis.setDireccionIP(this);
        setContentView(R.layout.activity_login);

        btInciarSesion = findViewById(R.id.btInciarSesion);
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
                txtContrasenha.getText().clear();

                // Iniciar la actividad de Registro
                startActivity(intent);
            }
        });

        btInciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Deshabilitar el botón para evitar múltiples clics
                btInciarSesion.setEnabled(false);
                login(txtEmail.getText().toString().toLowerCase(), txtContrasenha.getText().toString());
                Utilidades.ocultarTeclado(Login.this, v);
            }
        });

        btRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Registro.class);
                txtContrasenha.getText().clear();

                if(!checkboxRecordarEmail.isChecked()){
                    txtEmail.setText("");
                }
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
        servicioUsuario = Apis.getServicioUsuario(this);

        // Primero, verificar si el correo electrónico existe
        Call<List<modeloUsuario>> call = servicioUsuario.getUsuarioXEmail(email);

        call.enqueue(new Callback<List<modeloUsuario>>() {
            @Override
            public void onResponse(Call<List<modeloUsuario>> call, Response<List<modeloUsuario>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<modeloUsuario> usuarios = response.body();
                    if (!usuarios.isEmpty()) {
                        modeloUsuario usuario = usuarios.get(0);
                        nombreUsuario = usuario.getNombre();
                        String contrasenhaAlmacenada = usuario.getContrasenha();
                        if (verificarContrasenha(contrasenha, contrasenhaAlmacenada)) {

                            // Si la contraseña es correcta, proceder con la lógica de iniciar sesión
                            iniciarSesion(usuario);

                        } else {
                            mostrarToastError(getApplicationContext(), "Contraseña incorrecta");
                            // Rehabilitar el botón si hay un error
                            btInciarSesion.setEnabled(true);
                        }
                    } else {
                        mostrarToastError(getApplicationContext(), "Usuario incorrecto");
                        // Rehabilitar el botón si hay un error
                        btInciarSesion.setEnabled(true);
                    }
                } else {
                    mostrarToastError(getApplicationContext(), "Email o contraseña incorrectos");
                    // Rehabilitar el botón si hay un error
                    btInciarSesion.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<List<modeloUsuario>> call, Throwable throwable) {
                Log.e("Error", throwable.toString());
                mostrarToastError(getApplicationContext(), "Error de red");
                // Rehabilitar el botón si hay un error
                btInciarSesion.setEnabled(true);
            }
        });
    }

    private void iniciarSesion(modeloUsuario usuario) {
        servicioUsuario = Apis.getServicioUsuario(this);
        Call<Map<String, Object>> call = servicioUsuario.iniciarSesion(usuario);

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> responseBody = response.body();
                    String idPedido = String.valueOf(responseBody.get("idPedido"));
                    List<modeloDetallePedido> detallesPedidos = (List<modeloDetallePedido>) responseBody.get("detallesPedido");

                    System.out.println("ID del pedido recibido en la app: " + idPedido);

                    Intent intent = new Intent(Login.this, MainActivity.class);
                    intent.putExtra("nombreUsuario", nombreUsuario);
                    intent.putExtra("idPedido", idPedido);

                    if (!checkboxRecordarEmail.isChecked()) {
                        limpiarCampos();
                    } else {
                        txtContrasenha.getText().clear();
                    }

                    cargandoAlert.startAlertDialog();
                    cargarMainActivity(intent);
                } else {
                    mostrarToastError(getApplicationContext(), "Error al iniciar sesión");
                    // Rehabilitar el botón si hay un error
                    btInciarSesion.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable throwable) {
                Log.e("Error", throwable.toString());
                mostrarToastError(getApplicationContext(), "Error de red");
                // Rehabilitar el botón si hay un error
                btInciarSesion.setEnabled(true);
            }
        });
    }

    public void cargarMainActivity(Intent intent) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            cargandoAlert.closeAlertDialog();
            startActivity(intent);
            // Rehabilitar el botón después de la transición
            btInciarSesion.setEnabled(true);
        }, 2000);
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
