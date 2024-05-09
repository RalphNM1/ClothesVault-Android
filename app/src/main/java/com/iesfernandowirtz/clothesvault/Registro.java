package com.iesfernandowirtz.clothesvault;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.iesfernandowirtz.clothesvault.Modelo.Usuario;
import com.iesfernandowirtz.clothesvault.Utils.Apis;
import com.iesfernandowirtz.clothesvault.Utils.ServicioUsuario;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Registro extends AppCompatActivity {

    ServicioUsuario servicioUsuario;
    EditText txtNombre;
    EditText txtPrimerApellido;
    EditText txtSegundoApellido;
    EditText txtEmail;
    EditText txtContrasenha;
    EditText txtDireccion;
    EditText txtCp;
    Button btRegistrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro);

        txtNombre = (EditText) findViewById(R.id.registerNombre);
        txtPrimerApellido = (EditText) findViewById(R.id.registerApelldio1);
        txtSegundoApellido = (EditText) findViewById(R.id.registerApelldio2);
        txtEmail = (EditText) findViewById(R.id.registerEmail);
        txtContrasenha = (EditText) findViewById(R.id.registerContrasenha);
        txtDireccion = (EditText) findViewById(R.id.registerDirrecion);
        txtCp = (EditText) findViewById(R.id.registerCP);
        btRegistrar = (Button) findViewById(R.id.btRegistrar);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;


        });


        btRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Usuario u = new Usuario();
                    u.setNombre(txtNombre.getText().toString());
                    u.setApellido1(txtPrimerApellido.getText().toString());
                    u.setApellido2(txtSegundoApellido.getText().toString());
                    String email = txtEmail.getText().toString();
                    if (!validarFormatoCorreo(email)) {
                        Toast.makeText(Registro.this, "Ingrese un correo electrónico válido", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    u.setEmail(email);
                    u.setContrasenha(cifrarContrasenha(txtContrasenha.getText().toString())); // CIFRAR CONTRASEÑA
                    u.setDireccion(txtDireccion.getText().toString());
                    u.setCp(Integer.parseInt(txtCp.getText().toString()));

                    // Comprobar que el usuario no existe
                    verificarUsuarioExistente(u);
                } catch (NumberFormatException nfe) {
                    Toast.makeText(Registro.this, "Introduzca todos los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });

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


    public void limpiarCampos() { // Limpiar todos los campos de la pantalla
        txtNombre.setText("");
        txtPrimerApellido.setText("");
        txtSegundoApellido.setText("");
        txtEmail.setText("");
        txtContrasenha.setText("");
        txtDireccion.setText("");
        txtCp.setText("");
    }

    private boolean validarFormatoCorreo(String correo) {
        // Expresión regular para validar el formato del correo electrónico
        String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return correo.matches(regex);
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

    public void abrirOtraActividad(View view) {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
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