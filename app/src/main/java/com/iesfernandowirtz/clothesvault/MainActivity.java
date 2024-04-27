// MainActivity.java
package com.iesfernandowirtz.clothesvault;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.iesfernandowirtz.clothesvault.Modelo.Producto;
import com.iesfernandowirtz.clothesvault.Modelo.Usuario;
import com.iesfernandowirtz.clothesvault.Utils.Apis;
import com.iesfernandowirtz.clothesvault.Utils.ServicioProducto;
import com.iesfernandowirtz.clothesvault.Utils.ServicioUsuario;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    ListView listView;


    ServicioProducto servicioProducto;
    List<Producto> listaProductos= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        listView = (ListView) findViewById(R.id.listView);
        servicioProducto = Apis.getServicioProducto();

        listarUsuarios();


    }

    public void listarUsuarios() {

        Call<List<Producto>> call = servicioProducto.getProducto();

        call.enqueue(new Callback<List<Producto>>() {

            @Override
            public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaProductos = response.body();
                    for (Producto producto : listaProductos) {
                        Log.e("Usuario", "Nombre: " + producto.getNombre() + ", Descripcion: " + producto.getDescripcion() + ", Precio: " + producto.getPrecio());
                    }
                    // Configura el adaptador solo una vez, fuera del bucle for
                    listView.setAdapter(new AdaptadorProducto(MainActivity.this, R.layout.activity_main, listaProductos));
                } else {
                    // Maneja la respuesta no exitosa o el cuerpo nulo
                    Log.e("onResponse", "Error en la respuesta: " + response.toString());
                }
            }

            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {
                Log.e("Error:", t.getMessage());
            }
        });
    }

}
