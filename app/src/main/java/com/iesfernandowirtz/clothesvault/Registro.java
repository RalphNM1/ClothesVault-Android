package com.iesfernandowirtz.clothesvault;

// Importaciones necesarias
import static com.iesfernandowirtz.clothesvault.Utilidades.mostrarToastError;
import static com.iesfernandowirtz.clothesvault.Utilidades.mostrarToastSuccess;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import com.iesfernandowirtz.clothesvault.modelo.modeloUsuario;
import com.iesfernandowirtz.clothesvault.utils.Apis;
import com.iesfernandowirtz.clothesvault.utils.ServicioUsuario;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Registro extends ActividadBase {

    ServicioUsuario servicioUsuario;
    EditText txtNombre;
    EditText txtPrimerApellido;
    EditText txtSegundoApellido;
    EditText txtEmail;
    EditText txtContrasenha;
    EditText txtDireccion;
    EditText txtCp;
    Button btRegistrar;
    ImageView btAtras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // Inicialización de los elementos de la interfaz de usuario
        txtNombre = (EditText) findViewById(R.id.registerNombre);
        txtPrimerApellido = (EditText) findViewById(R.id.registerApelldio1);
        txtSegundoApellido = (EditText) findViewById(R.id.registerApelldio2);
        txtEmail = (EditText) findViewById(R.id.registerEmail);
        txtContrasenha = (EditText) findViewById(R.id.registerContrasenha);
        txtDireccion = (EditText) findViewById(R.id.registerDirrecion);
        txtCp = (EditText) findViewById(R.id.registerCP);
        btRegistrar = (Button) findViewById(R.id.btRegistrar);
        btAtras = (ImageView) findViewById(R.id.btAtras);

        // Configuración de un listener para ocultar el teclado virtual cuando se toca fuera de los campos de texto
        View container = findViewById(R.id.ScrollViewRegistro);
        container.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Utilidades.ocultarTeclado(Registro.this, v);
                return false;
            }
        });

        // Configuración de un listener para el botón de retroceso
        btAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Configuración de un listener para validar la contraseña mientras se está escribiendo
        txtContrasenha.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!txtContrasenha.getText().toString().isEmpty()){
                    validarContrasenha(txtContrasenha.getText().toString(), txtContrasenha);
                }
            }
        });

        // Configuración de un listener para el botón de registro
        btRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (comprobarCampos()) {
                        // Creación de un nuevo usuario con la información ingresada
                        modeloUsuario u = new modeloUsuario();
                        u.setNombre(txtNombre.getText().toString());
                        u.setApellido1(txtPrimerApellido.getText().toString());
                        u.setApellido2(txtSegundoApellido.getText().toString());
                        String email = txtEmail.getText().toString().toLowerCase();
                        if (!Utilidades.validarFormatoCorreo(email)) {
                            txtEmail.requestFocus();
                            txtEmail.setError("Ingrese un correo electrónico válido");
                            return;
                        }
                        u.setEmail(email);

                        // Validación de la contraseña
                        if (!validarContrasenha(txtContrasenha.getText().toString(), txtContrasenha)) {
                            return;
                        }

                        // Cifrado de la contraseña antes de guardarla
                        u.setContrasenha(cifrarContrasenha(txtContrasenha.getText().toString())); // CIFRAR CONTRASEÑA
                        u.setDireccion(txtDireccion.getText().toString());
                        u.setCp(Integer.parseInt(txtCp.getText().toString()));

                        // Verificación de si el usuario ya existe
                        verificarUsuarioExistente(u);
                    }

                } catch (NumberFormatException nfe) {
                    // Manejo de excepciones si no se introducen todos los campos requeridos
                    mostrarToastError(getApplicationContext(), "Introduzca todos los campos");
                }

            }
        });

        // Configuración del color de la barra de estado si el dispositivo está ejecutando Android Lollipop o superior
        Utilidades.cambiarColorBarraDeEstado(getApplicationContext(),getWindow(),R.color.colorFondo);

    }

    // Método para mostrar un mensaje de error en un campo de texto
    private void mostrarError(EditText editText) {
        editText.requestFocus();
        editText.setError("Campo Obligatorio");
    }

    // Método para comprobar si todos los campos obligatorios están completos
    private boolean comprobarCampos() {
        boolean todoOK = true;

        if (txtNombre.getText().toString().isEmpty()) {
            mostrarError(txtNombre);
            todoOK = false;
        }
        if (txtPrimerApellido.getText().toString().isEmpty()) {
            mostrarError(txtPrimerApellido);
            todoOK = false;
        }

        if (txtSegundoApellido.getText().toString().isEmpty()) {
            mostrarError(txtSegundoApellido);
            todoOK = false;
        }

        if (txtEmail.getText().toString().isEmpty()) {
            mostrarError(txtEmail);
            todoOK = false;
        }

        if (txtContrasenha.getText().toString().isEmpty()) {
            mostrarError(txtContrasenha);
            todoOK = false;
        }

        if (txtDireccion.getText().toString().isEmpty()) {
            mostrarError(txtDireccion);
            todoOK = false;
        }

        if (txtCp.getText().toString().isEmpty()) {
            mostrarError(txtCp);
            todoOK = false;
        }

        return todoOK;

    }

    // Método para verificar si un usuario ya existe en el servidor
    private void verificarUsuarioExistente(modeloUsuario u) {
        servicioUsuario = Apis.getServicioUsuario(this);

        // Construcción de la URL completa con el correo electrónico proporcionado
        String email = u.getEmail();
        Call<List<modeloUsuario>> call = servicioUsuario.getUsuarioXEmail(email);

        call.enqueue(new Callback<List<modeloUsuario>>() {
            @Override
            public void onResponse(Call <List<modeloUsuario>> call, Response<List<modeloUsuario>> response) {

                if (response.body().isEmpty()) {
                    // Si el usuario no existe, se muestra un mensaje de éxito y se agrega al servidor
                    mostrarToastSuccess(getApplicationContext(), "Creando Usuario...");
                    addUsuario(u);
                    limpiarCampos(); // Se limpian los campos después de agregar el usuario
                } else {
                    // Si el usuario ya existe, se muestra un mensaje de error
                    mostrarToastError(getApplicationContext(), "El usuario ya existe");
                }
            }

            @Override
            public void onFailure(Call<List<modeloUsuario>> call, Throwable throwable) {
                // Manejo de fallos en la comunicación con el servidor
            }
        });
    }

    // Método para validar la fortaleza de la contraseña
    private boolean validarContrasenha(String password, EditText editText) {
        StringBuilder validationMessage = new StringBuilder();
        boolean isValid = true;

        // Se comprueba si la contraseña cumple con ciertos criterios
        if (!password.matches(".*[a-z].*")) {
            validationMessage.append("- Debe tener al menos una letra minúscula.\n");
            isValid = false;
        }
        if (!password.matches(".*[A-Z].*")) {
            validationMessage.append("- Debe tener al menos una letra mayúscula.\n");
            isValid = false;
        }
        if (!password.matches(".*\\d.*")) {
            validationMessage.append("- Debe tener al menos un número.\n");
            isValid = false;
        }
        if (password.length() < 8) {
            validationMessage.append("- Debe tener al menos 8 caracteres.\n");
            isValid = false;
        }

        // Añade tu lógica para verificar contraseñas comunes aquí, si tienes una lista de contraseñas comunes.

        // Si la contraseña no es válida, se muestra un mensaje de error
        if (!isValid) {
            editText.setError(validationMessage.toString().trim());
        } else {
            editText.setError(null);
        }

        return isValid;
    }

    // Método para limpiar todos los campos de la pantalla
    public void limpiarCampos() {
        txtNombre.setText("");
        txtPrimerApellido.setText("");
        txtSegundoApellido.setText("");
        txtEmail.setText("");
        txtContrasenha.setText("");
        txtDireccion.setText("");
        txtCp.setText("");
    }

    // Método para agregar un nuevo usuario al servidor
    public void addUsuario(modeloUsuario u) {
        servicioUsuario = Apis.getServicioUsuario(this);
        Call<modeloUsuario> call = servicioUsuario.addUsuario(u);

        call.enqueue(new Callback<modeloUsuario>() {
            @Override
            public void onResponse(Call<modeloUsuario> call, Response<modeloUsuario> response) {
                if (response != null) {
                    // Si la operación se realiza correctamente, se muestra un mensaje de éxito
                    mostrarToastSuccess(getApplicationContext(), "Se agrego con éxito");
                }
            }

            @Override
            public void onFailure(Call<modeloUsuario> call, Throwable throwable) {
                // Manejo de fallos en la comunicación con el servidor
                Log.e("Error: ", throwable.getMessage());
            }
        });
    }

    // Método para cifrar la contraseña utilizando el algoritmo SHA-256
    public static String cifrarContrasenha(String contrasenha) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(contrasenha.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // Manejo de excepciones si no se encuentra el algoritmo de cifrado
            e.printStackTrace();
            return null;
        }
    }
}

