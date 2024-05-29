// MainActivity.java
package com.iesfernandowirtz.clothesvault;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iesfernandowirtz.clothesvault.Modelo.Producto;
import com.iesfernandowirtz.clothesvault.Utils.Apis;
import com.iesfernandowirtz.clothesvault.Utils.ServicioProducto;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends ActividadBase {

    ListView listView;
    TextView saludo;
    ServicioProducto servicioProducto;
    private RecyclerView recyclerView;
    private AdaptadorProducto adaptadorProducto;
    private List<Producto> productoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EdgeToEdge.enable(this);
        saludo = (TextView) findViewById(R.id.tvSaludo);
        servicioProducto = Apis.getServicioProducto(this);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorToolbar));
        }

        // Obtener el nombre de usuario del Intent
        Intent intent = getIntent();
        String nombreUsuario = intent.getStringExtra("nombreUsuario");


        String idiomaActual = Locale.getDefault().getLanguage();

        String textoSaludo;

        if (idiomaActual.equals("es")) {
            textoSaludo = "¡Hola, ";
        } else {
            textoSaludo = "¡Ola, ";
        }

        // Usar el nombreUsuario como sea necesario, por ejemplo, para mostrar un saludo
        if (nombreUsuario != null) {
            saludo.setText(textoSaludo + nombreUsuario + "!");
        }





        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 columnas

        productoList = new ArrayList<>();

        // Configura el adaptador con la lista vacía inicialmente
        adaptadorProducto = new AdaptadorProducto(this, productoList);
        recyclerView.setAdapter(adaptadorProducto);

        // Realiza la solicitud HTTP para obtener los productos desde la API
        obtenerProductosDesdeAPI();



    }

    private void obtenerProductosDesdeAPI() {
        // Utiliza Retrofit para realizar la solicitud HTTP
        Call<List<Producto>> call = servicioProducto.getProducto();

        call.enqueue(new Callback<List<Producto>>() {
            @Override
            public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Si la solicitud fue exitosa y se obtuvieron los datos
                    productoList.addAll(response.body());
                    adaptadorProducto.notifyDataSetChanged(); // Notifica al adaptador que los datos han cambiado
                } else {
                    // Manejar la respuesta no exitosa o null
                    Log.e("Error", "Error1");
                }
            }

            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {
                // Manejar el fallo de la solicitud
                Log.e("Error", t.getMessage());

            }
        });
    }

}
