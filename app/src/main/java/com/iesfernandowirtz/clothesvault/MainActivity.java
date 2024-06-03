// MainActivity.java
package com.iesfernandowirtz.clothesvault;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.iesfernandowirtz.clothesvault.Modelo.Categoria;
import com.iesfernandowirtz.clothesvault.Modelo.Producto;
import com.iesfernandowirtz.clothesvault.Utils.Apis;
import com.iesfernandowirtz.clothesvault.Utils.ServicioCategoria;
import com.iesfernandowirtz.clothesvault.Utils.ServicioProducto;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends ActividadBase {

    TextView saludo;
    ServicioProducto servicioProducto;

    ServicioCategoria servicioCategoria;
    private RecyclerView recyclerView;
    private AdaptadorProducto adaptadorProducto;
    private List<Producto> productoList;
    private List<Producto> productoListFiltrada;
    private String categoriaSeleccionada;
    private String marcaSeleccionada;
    private List<String> categorias;
    private List<String> marcas;
    private List<Categoria> categoriasList; // Lista de categorías con IDs
    private int posicionCategoriaSeleccionada = -1; // Inicialmente sin selección
    private int posicionMarcaSeleccionada = -1; // Inicialmente sin selección
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EdgeToEdge.enable(this);
        saludo = findViewById(R.id.tvSaludo);
        servicioProducto = Apis.getServicioProducto(this);
        servicioCategoria = Apis.getServicioCategoria(this);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorToolbar));
        }

        // Obtener el nombre de usuario del Intent
        Intent intent = getIntent();
        String nombreUsuario = intent.getStringExtra("nombreUsuario");

        String idiomaActual = Locale.getDefault().getLanguage();
        String textoSaludo = idiomaActual.equals("es") ? "¡Hola, " : "¡Ola, ";

        // Usar el nombreUsuario como sea necesario, por ejemplo, para mostrar un saludo
        if (nombreUsuario != null) {
            saludo.setText(textoSaludo + nombreUsuario + "!");
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 columnas

        productoListFiltrada = new ArrayList<>();
        productoList = new ArrayList<>();

        // Configura el adaptador con la lista vacía inicialmente
        adaptadorProducto = new AdaptadorProducto(this, productoListFiltrada);
        recyclerView.setAdapter(adaptadorProducto);

        // Realiza la solicitud HTTP para obtener los productos desde la API
        obtenerProductosDesdeAPI();
        obtenerCategoriasDesdeAPI();
        obtenerMarcasDesdeAPI();

        // Configurar el click listener para el iconFiltro
        ImageView iconFiltro = findViewById(R.id.iconFiltro);
        iconFiltro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoFiltros();
            }
        });
    }

    private void obtenerProductosDesdeAPI() {
        Call<List<Producto>> call = servicioProducto.getProducto();

        call.enqueue(new Callback<List<Producto>>() {
            @Override
            public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Registro de la respuesta JSON en la consola
                    Log.d("Respuesta JSON", new Gson().toJson(response.body()));

                    productoList.clear();
                    productoList.addAll(response.body());

                    productoListFiltrada.addAll(productoList);
                    adaptadorProducto.notifyDataSetChanged();
                } else {
                    Log.e("Error", "Error1");
                }
            }

            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {
                Log.e("Error", t.getMessage());
            }
        });
    }

    private void obtenerProductosDesdeAPI(Long categoriaId, String marca) {
        Call<List<Producto>> call;
        if (marca != null && marca.equals("Todas")) {
            // Si la marca es "Todas", obtenemos todos los productos de la categoría especificada
            if (categoriaId != null) {
                call = servicioProducto.getProductosPorCategoria(categoriaId);
            } else {
                // Si no se especifica una categoría, obtenemos todos los productos
                call = servicioProducto.getProducto();
            }
        } else {
            // Si se especifica una marca específica, filtramos por categoría y marca
            call = servicioProducto.getProductosPorCategoria(categoriaId, marca);
        }

        call.enqueue(new Callback<List<Producto>>() {
            @Override
            public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productoList.clear();
                    productoList.addAll(response.body());
                    productoListFiltrada.clear();
                    productoListFiltrada.addAll(productoList);
                    adaptadorProducto.notifyDataSetChanged();
                } else {
                    Log.e("Error", response.toString());
                }
            }

            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {
                Log.e("Error", "Error en la solicitud HTTP: " + t.getMessage());
            }
        });
    }



    private void obtenerCategoriasDesdeAPI() {
        Call<List<Categoria>> call = servicioCategoria.getCategorias();

        call.enqueue(new Callback<List<Categoria>>() {
            @Override
            public void onResponse(Call<List<Categoria>> call, Response<List<Categoria>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categorias = new ArrayList<>();
                    categorias.add("Todas");
                    categoriasList = response.body(); // Almacena la lista de categorías
                    for (Categoria categoria : categoriasList) {
                        categorias.add(categoria.getNombre());
                    }
                } else {
                    Log.e("Error", "Error al obtener categorías");
                    Log.e("Error", response.toString());
                }
            }

            @Override
            public void onFailure(Call<List<Categoria>> call, Throwable t) {
                Log.e("Error", t.getMessage());
            }
        });
    }

    private void obtenerMarcasDesdeAPI() {
        Call<List<String>> call = servicioProducto.getMarcas();

        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    marcas = new ArrayList<>();
                    marcas.add("Todas");
                    marcas.addAll(response.body());

                } else {
                    Log.e("Error", "Error al obtener marcas");
                    Log.e("Error", response.toString());
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.e("Error", t.getMessage());
            }
        });
    }
    private void obtenerCategorias() {
        Call<List<Categoria>> call = servicioCategoria.getCategorias();

        call.enqueue(new Callback<List<Categoria>>() {
            @Override
            public void onResponse(Call<List<Categoria>> call, Response<List<Categoria>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    Log.d("Respuesta JSON", new Gson().toJson(response.body()));

                } else {
                    Log.e("Error", "Error al obtener categorías");
                    Log.e("Error", response.toString());
                }
            }

            @Override
            public void onFailure(Call<List<Categoria>> call, Throwable t) {
                Log.e("Error", t.getMessage());
            }
        });
    }
    private void mostrarDialogoFiltros() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_filtros);

        final Spinner spinnerCategorias = dialog.findViewById(R.id.spinnerCategorias);
        final Spinner spinnerMarcas = dialog.findViewById(R.id.spinnerMarcas);
        Button btnAplicarFiltros = dialog.findViewById(R.id.btnAplicarFiltros);
        Button btnQuitarFiltros = dialog.findViewById(R.id.btnQuitarFiltros);

        // Configurar los spinners con datos de la API
        if (categorias != null && marcas != null) {
            ArrayAdapter<String> adapterCategorias = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categorias);
            adapterCategorias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCategorias.setAdapter(adapterCategorias);

            ArrayAdapter<String> adapterMarcas = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, marcas);
            adapterMarcas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerMarcas.setAdapter(adapterMarcas);

            // Restaurar el estado de los spinners
            if (posicionCategoriaSeleccionada != -1) {
                spinnerCategorias.setSelection(posicionCategoriaSeleccionada);
            }

            if (posicionMarcaSeleccionada != -1) {
                spinnerMarcas.setSelection(posicionMarcaSeleccionada);
            }
        }

        btnAplicarFiltros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoriaSeleccionada = spinnerCategorias.getSelectedItem().toString();
                marcaSeleccionada = spinnerMarcas.getSelectedItem().toString();
                // Guardar las posiciones seleccionadas
                posicionCategoriaSeleccionada = spinnerCategorias.getSelectedItemPosition();
                posicionMarcaSeleccionada = spinnerMarcas.getSelectedItemPosition();

                if (!categoriaSeleccionada.equals("Todas")) {
                    // Obtener el ID de la categoría seleccionada
                    Long categoriaId = null;
                    for (Categoria categoria : categoriasList) { // Buscar el ID de la categoría seleccionada
                        if (categoria.getNombre().equals(categoriaSeleccionada)) {
                            categoriaId = categoria.getId();
                            break;
                        }
                    }
                    if (categoriaId != null) {
                        obtenerProductosDesdeAPI(categoriaId, marcaSeleccionada);
                    }
                } else {
                    obtenerProductosDesdeAPI(null, marcaSeleccionada); // Cargar todos los productos si se selecciona "Todas"
                }

                aplicarFiltros();
                dialog.dismiss();
            }
        });

        btnQuitarFiltros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoriaSeleccionada = null;
                marcaSeleccionada = null;
                posicionCategoriaSeleccionada = -1; // Reiniciar la posición de la selección
                posicionMarcaSeleccionada = -1; // Reiniciar la posición de la selección
                obtenerProductosDesdeAPI();
                productoListFiltrada.clear();
                productoList.clear();// Limpiar la lista filtrada
                productoListFiltrada.addAll(productoList); // Actualizar la lista filtrada con todos los productos
                adaptadorProducto.notifyDataSetChanged();
                dialog.dismiss();
            }
        });


        dialog.show();
    }


    private void aplicarFiltros() {
        productoListFiltrada.clear();
        for (Producto producto : productoList) {
            boolean coincideCategoria = (categoriaSeleccionada == null || categoriaSeleccionada.equals("Todas") ||
                    (producto.getCategoria() != null && producto.getCategoria().getNombre().equals(categoriaSeleccionada)));

            boolean coincideMarca = (marcaSeleccionada == null || marcaSeleccionada.equals("Todas") ||
                    producto.getMarca().equals(marcaSeleccionada));

            if (coincideCategoria && coincideMarca) {
                productoListFiltrada.add(producto);
            }
        }
        adaptadorProducto.notifyDataSetChanged();
    }


}