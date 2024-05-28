package com.iesfernandowirtz.clothesvault;

import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.iesfernandowirtz.clothesvault.Modelo.Usuario;
import com.iesfernandowirtz.clothesvault.Utils.Apis;
import com.iesfernandowirtz.clothesvault.Utils.ServicioUsuario;

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
    TextView passwordValidation;  // TextView para mostrar mensajes de validación de la contraseña

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);


        txtNombre = (EditText) findViewById(R.id.registerNombre);
        txtPrimerApellido = (EditText) findViewById(R.id.registerApelldio1);
        txtSegundoApellido = (EditText) findViewById(R.id.registerApelldio2);
        txtEmail = (EditText) findViewById(R.id.registerEmail);
        txtContrasenha = (EditText) findViewById(R.id.registerContrasenha);
        txtDireccion = (EditText) findViewById(R.id.registerDirrecion);
        txtCp = (EditText) findViewById(R.id.registerCP);
        btRegistrar = (Button) findViewById(R.id.btRegistrar);
        btAtras = (ImageView) findViewById(R.id.btAtras);

        View container = findViewById(R.id.ScrollViewRegistro);
        container.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Utilidades.ocultarTeclado(Registro.this, v);
                return false;
            }
        });

        btAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Registro.this, Login.class);
                startActivity(intent);
            }
        });

        txtContrasenha.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                validarContrasenha(txtContrasenha.getText().toString(), txtContrasenha);
            }
        });


        btRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (comprobarCampos()) {


                        Usuario u = new Usuario();
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

                        if (!validarContrasenha(txtContrasenha.getText().toString(), txtContrasenha)) {
                            return;
                        }

                        u.setContrasenha(cifrarContrasenha(txtContrasenha.getText().toString())); // CIFRAR CONTRASEÑA
                        u.setDireccion(txtDireccion.getText().toString());
                        u.setCp(Integer.parseInt(txtCp.getText().toString()));

                        // Comprobar que el usuario no existe
                        verificarUsuarioExistente(u);
                    }

                } catch (NumberFormatException nfe) {
                    Toast.makeText(Registro.this, "Introduzca todos los campos", Toast.LENGTH_SHORT).show();
                }

            }

        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorFondo));
        }
    }

    private void mostrarError(EditText editText) {
        editText.requestFocus();
        editText.setError("Campo Obligatorio");
    }

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


    private void verificarUsuarioExistente(Usuario u) {
        servicioUsuario = Apis.getServicioUsuario();

        // Construir la URL completa con el correo electrónico proporcionado
        String email = u.getEmail();
        Call<List<Usuario>> call = servicioUsuario.getUsuarioXEmail(email);


        call.enqueue(new Callback<List<Usuario>>() {

            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {

                if (response.body().isEmpty()) {
                    Toast.makeText(Registro.this, "Creando Usuario...", Toast.LENGTH_SHORT).show();
                    addUsuario(u);
                    limpiarCampos();
                } else {
                    Toast.makeText(Registro.this, "El usuario ya existe", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable throwable) {

            }
        });
    }

    private boolean validarContrasenha(String password, EditText editText) {
        StringBuilder validationMessage = new StringBuilder();
        boolean isValid = true;

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

        if (!isValid) {
            editText.setError(validationMessage.toString().trim());
        } else {
            editText.setError(null);
        }

        return isValid;
    }

    public void limpiarCampos() { // Limpiar todos los campos de la pantalla
        txtNombre.setText("");
        txtPrimerApellido.setText("");
        txtSegundoApellido.setText("");
        txtEmail.setText("");
        txtContrasenha.setText("");
        txtDireccion.setText("");
        txtCp.setText("");
    }


    public void addUsuario(Usuario u) {
        servicioUsuario = Apis.getServicioUsuario();
        Call<Usuario> call = servicioUsuario.addUsuario(u);

        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response != null) {
                    Toast.makeText(Registro.this, "Se agrego con éxito", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable throwable) {
                Log.e("Error: ", throwable.getMessage());
            }
        });
    }


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
            e.printStackTrace();
            return null;
        }
    }
}