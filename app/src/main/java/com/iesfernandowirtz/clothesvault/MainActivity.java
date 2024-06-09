// MainActivity.java
package com.iesfernandowirtz.clothesvault;

// Importaciones de librerías y utilidades
import static com.iesfernandowirtz.clothesvault.Utilidades.mostrarToastError;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

// Importaciones de modelos y servicios
import com.bumptech.glide.Glide;
import com.iesfernandowirtz.clothesvault.modelo.Categoria;
import com.iesfernandowirtz.clothesvault.modelo.modeloProducto;
import com.iesfernandowirtz.clothesvault.modelo.modeloUsuario;
import com.iesfernandowirtz.clothesvault.utils.Apis;
import com.iesfernandowirtz.clothesvault.utils.ServicioCategoria;
import com.iesfernandowirtz.clothesvault.utils.ServicioProducto;
import com.iesfernandowirtz.clothesvault.utils.ServicioUsuario;

// Importaciones adicionales
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.Map;

// Declaración de la clase MainActivity
public class MainActivity extends ActividadBase implements AdaptadorProducto.OnProductoClickListener {

    // Declaración de variables de la actividad
    private TextView saludo;
    private ServicioProducto servicioProducto;
    private ServicioUsuario servicioUsuario;
    private ServicioCategoria servicioCategoria;
    private RecyclerView recyclerView;
    private AdaptadorProducto adaptadorProducto;
    private List<modeloProducto> productoList;
    private String categoriaSeleccionada;
    private String marcaSeleccionada;
    private List<String> categorias;
    private List<String> marcas;
    private List<Categoria> categoriasList; // Lista de categorías con IDs
    private int posicionCategoriaSeleccionada = -1; // Inicialmente sin selección
    private int posicionMarcaSeleccionada = -1; // Inicialmente sin selección
    private TextView tvRespuesta;
    private ImageView iconFiltro;
    private ImageView iconCarrito;
    private ImageView btAtras;
    private boolean dialogoMostrado = false;
    private String idPedido;
    private String email;
    private Intent intentCarrito;
    private static final int CODIGO_SOLICITUD_CARRO = 1;

    // Método onCreate, se ejecuta al iniciar la actividad
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EdgeToEdge.enable(this);

        // Inicialización de vistas y servicios
        saludo = findViewById(R.id.tvSaludo);
        servicioProducto = Apis.getServicioProducto(this);
        servicioCategoria = Apis.getServicioCategoria(this);
        tvRespuesta = findViewById(R.id.tvRespuesta);
        iconFiltro = findViewById(R.id.iconFiltro);
        iconCarrito = findViewById(R.id.iconCarrito);
        btAtras = findViewById(R.id.btAtras);
        servicioUsuario = Apis.getServicioUsuario(this);

        intentCarrito = new Intent(MainActivity.this, CarritoActivity.class);

        // Recuperación del email desde el intent
        email = getIntent().getStringExtra("email");
        System.out.println("email recibido en MainActivity: " + email);
        usuarioXEmail(email);

        // Configuración del color de la barra de estado
        Utilidades.cambiarColorBarraDeEstado(getApplicationContext(),getWindow(),R.color.colorFondo);

        // Obtener el nombre de usuario del Intent
        Intent intent = getIntent();
        String nombreUsuario = intent.getStringExtra("nombreUsuario");

        String idiomaActual = Locale.getDefault().getLanguage();
        String textoSaludo = idiomaActual.equals("es") ? "¡Hola, " : "¡Ola, ";

        if (nombreUsuario != null) {
            saludo.setText(textoSaludo + nombreUsuario + "!");
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 columnas

        productoList = new ArrayList<>();

        adaptadorProducto = new AdaptadorProducto(this, productoList);
        adaptadorProducto.setOnProductoClickListener(this);
        recyclerView.setAdapter(adaptadorProducto);

        obtenerProductosDesdeAPI();
        obtenerCategoriasDesdeAPI();
        obtenerMarcasDesdeAPI();

        // Configuración de listeners para los botones
        btAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        iconFiltro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoFiltros();
            }
        });
        iconCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(intentCarrito, CODIGO_SOLICITUD_CARRO);
            }
        });

    }

    // Método para manejar el resultado de la actividad
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODIGO_SOLICITUD_CARRO && resultCode == Activity.RESULT_OK) {
            usuarioXEmail(email);
        }
    }

    // Método para obtener el usuario por email desde la API
    private void usuarioXEmail(String email) {
        servicioUsuario = Apis.getServicioUsuario(this);

        Call<List<modeloUsuario>> call = servicioUsuario.getUsuarioXEmail(email);

        call.enqueue(new Callback<List<modeloUsuario>>() {
            @Override
            public void onResponse(Call<List<modeloUsuario>> call, Response<List<modeloUsuario>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<modeloUsuario> usuarios = response.body();
                    if (!usuarios.isEmpty()) {
                        modeloUsuario usuario = usuarios.get(0);
                        buscarCrearPedido(usuario);
                    } else {
                        mostrarToastError(getApplicationContext(), "Usuario incorrecto");
                    }
                } else {
                    mostrarToastError(getApplicationContext(), "Email incorrecto");
                }
            }

            @Override
            public void onFailure(Call<List<modeloUsuario>> call, Throwable throwable) {
                Log.e("Error", throwable.toString());
                mostrarToastError(getApplicationContext(), "Error de red");
            }
        });
    }

    // Método para buscar o crear un pedido para el usuario
    private void buscarCrearPedido(modeloUsuario usuario) {
        servicioUsuario = Apis.getServicioUsuario(this);
        Call<Map<String, Object>> call = servicioUsuario.crearPedidos(usuario);

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> responseBody = response.body();
                    idPedido = String.valueOf(responseBody.get("idPedido"));

                    System.out.println("ID del pedido recibido en la app: " + idPedido);

                    intentCarrito.putExtra("idPedido", idPedido); // Aquí se pasa el idPedido
                    intentCarrito.putExtra("email", email);
                } else {
                    mostrarToastError(getApplicationContext(), "Error al iniciar sesión");
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable throwable) {
                Log.e("Error", throwable.toString());
                mostrarToastError(getApplicationContext(), "Error de red");
            }
        });
    }

    // Método para mostrar un diálogo con los detalles del producto seleccionado
    @Override
    public void onProductoClick(modeloProducto producto) {
        // Verifica si el diálogo ya está mostrándose
        if (!dialogoMostrado) {
            // Si el diálogo no está mostrándose, muestra el diálogo y establece la variable a true
            mostrarDialogoProductoCatalago(producto);
            dialogoMostrado = true;
        }
    }

    // Método para mostrar el diálogo de detalles del producto seleccionado
    public void mostrarDialogoProductoCatalago(modeloProducto producto) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_producto_comprar);

        // Inicialización de vistas en el diálogo
        ImageView imageView = dialog.findViewById(R.id.productoImagen);
        TextView nombreTextView = dialog.findViewById(R.id.productoNombre);
        TextView descripcionTextView = dialog.findViewById(R.id.productoDesc);
        TextView precioTextView = dialog.findViewById(R.id.productoPrecio);
        TextView tallaTextView = dialog.findViewById(R.id.productoTalla);
        EditText cantidad = dialog.findViewById(R.id.etCantidad);
        Button btnmas = dialog.findViewById(R.id.btnmas);
        Button btnmenos = dialog.findViewById(R.id.btnmenos);
        Button btnComprar = dialog.findViewById(R.id.btnComprar);

        // Configuración de los datos del producto en el diálogo
        String imagenBase64 = producto.getImagen();
        if (imagenBase64 != null && !imagenBase64.isEmpty()) {
            byte[] imagenBytes = Base64.decode(imagenBase64, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imagenBytes, 0, imagenBytes.length);
            Glide.with(this).load(bitmap).placeholder(R.drawable.imagen_test).error(R.drawable.imagen_test).into(imageView);
        } else {
            imageView.setImageResource(R.drawable.imagen_test);
        }

        nombreTextView.setText(producto.getNombre());
        descripcionTextView.setText(producto.getDescripcion());
        DecimalFormat df = new DecimalFormat("#.00");
        String precioFormateado = df.format(producto.getPrecio());
        precioTextView.setText("Precio: " + precioFormateado + " €");
        tallaTextView.setText("Talla: " + producto.getTalla());

        // Configuración de los listeners de los botones en el diálogo
        btnmas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cantidadActual = Integer.parseInt(cantidad.getText().toString());
                cantidadActual++;
                cantidad.setText(String.valueOf(cantidadActual));
            }
        });

        btnmenos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cantidadActual = Integer.parseInt(cantidad.getText().toString());
                if (cantidadActual > 0) {
                    cantidadActual--;
                    cantidad.setText(String.valueOf(cantidadActual));
                }
            }
        });

        btnComprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cantidadActual = Integer.parseInt(cantidad.getText().toString());
                if (cantidadActual > 0) {
                    Utilidades.agregarProductoAlCarrito(servicioProducto, getApplicationContext(), producto.getId(), idPedido, cantidadActual);
                    Carrito.getInstance().agregarProducto(producto, cantidadActual);
                }
                dialog.dismiss();
            }
        });

        // Configuración del listener para cuando el diálogo se cierra
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dialogoMostrado = false;
            }
        });

        // Mostrar el diálogo
        dialog.show();
    }

    // Método para obtener todos los productos desde la API
    private void obtenerProductosDesdeAPI() {
        Call<List<modeloProducto>> call = servicioProducto.getProducto();

        call.enqueue(new Callback<List<modeloProducto>>() {
            @Override
            public void onResponse(Call<List<modeloProducto>> call, Response<List<modeloProducto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productoList.clear();
                    productoList.addAll(response.body());
                    verificarYMostrarRespuesta();
                } else {
                    Log.e("Error", "Error al obtener productos");
                }
            }

            @Override
            public void onFailure(Call<List<modeloProducto>> call, Throwable t) {
                Log.e("Error", t.getMessage());
            }
        });
    }

    // Método para obtener productos desde la API con filtros de categoría y marca
    private void obtenerProductosDesdeAPI(Long categoriaId, String nombreProveedor) {
        Call<List<modeloProducto>> call;
        if (nombreProveedor != null && nombreProveedor.equals("Todas")) {
            if (categoriaId != null) {
                call = servicioProducto.getProductosPorCategoria(categoriaId);
            } else {
                call = servicioProducto.getProducto();
            }
        } else {
            call = servicioProducto.getProductosPorCategoria(categoriaId, nombreProveedor);
        }

        call.enqueue(new Callback<List<modeloProducto>>() {
            @Override
            public void onResponse(Call<List<modeloProducto>> call, Response<List<modeloProducto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productoList.clear();
                    productoList.addAll(response.body());
                    verificarYMostrarRespuesta();
                } else {
                    Log.e("Error", response.toString());
                }
            }

            @Override
            public void onFailure(Call<List<modeloProducto>> call, Throwable t) {
                Log.e("Error", "Error en la solicitud HTTP: " + t.getMessage());
                mostrarToastError(getApplicationContext(), "No se ha podido establecer la conexión \n con la base de datos");
            }
        });
    }

    // Método para verificar si hay productos y mostrar la respuesta en la interfaz
    private void verificarYMostrarRespuesta() {
        if (productoList.isEmpty()) {
            tvRespuesta.setVisibility(View.VISIBLE);
        } else {
            tvRespuesta.setVisibility(View.INVISIBLE);
        }
        adaptadorProducto.notifyDataSetChanged();
    }

    // Método para obtener las categorías desde la API
    private void obtenerCategoriasDesdeAPI() {
        Call<List<Categoria>> call = servicioCategoria.getCategorias();

        call.enqueue(new Callback<List<Categoria>>() {
            @Override
            public void onResponse(Call<List<Categoria>> call, Response<List<Categoria>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categorias = new ArrayList<>();
                    categorias.add("Todas");
                    categoriasList = response.body();
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

    // Método para obtener las marcas desde la API
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

    // Método para mostrar el diálogo de filtros
    private void mostrarDialogoFiltros() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_filtros);

        final Spinner spinnerCategorias = dialog.findViewById(R.id.spinnerCategorias);
        final Spinner spinnerMarcas = dialog.findViewById(R.id.spinnerMarcas);
        Button btnAplicarFiltros = dialog.findViewById(R.id.btnAplicarFiltros);
        Button btnQuitarFiltros = dialog.findViewById(R.id.btnQuitarFiltros);

        if (categorias != null && marcas != null) {
            ArrayAdapter<String> adapterCategorias = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categorias);
            adapterCategorias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCategorias.setAdapter(adapterCategorias);

            ArrayAdapter<String> adapterMarcas = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, marcas);
            adapterMarcas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerMarcas.setAdapter(adapterMarcas);

            if (posicionCategoriaSeleccionada != -1) {
                spinnerCategorias.setSelection(posicionCategoriaSeleccionada);
            }

            if (posicionMarcaSeleccionada != -1) {
                spinnerMarcas.setSelection(posicionMarcaSeleccionada);
            }
        }

        // Configuración del botón de aplicar filtros
        btnAplicarFiltros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoriaSeleccionada = spinnerCategorias.getSelectedItem().toString();
                marcaSeleccionada = spinnerMarcas.getSelectedItem().toString();
                posicionCategoriaSeleccionada = spinnerCategorias.getSelectedItemPosition();
                posicionMarcaSeleccionada = spinnerMarcas.getSelectedItemPosition();

                if (categoriaSeleccionada.equals("Todas") && marcaSeleccionada.equals("Todas")) {
                    obtenerProductosDesdeAPI(null, null); // Cambio aquí
                } else if (!categoriaSeleccionada.equals("Todas")) {
                    Long categoriaId = null;
                    for (Categoria categoria : categoriasList) {
                        if (categoria.getNombre().equals(categoriaSeleccionada)) {
                            categoriaId = categoria.getId();
                            break;
                        }
                    }
                    obtenerProductosDesdeAPI(categoriaId, marcaSeleccionada); // Cambio aquí
                } else {
                    obtenerProductosDesdeAPI(null, marcaSeleccionada); // Cambio aquí
                }

                dialog.dismiss();
            }
        });

        // Configuración del botón de quitar filtros
        btnQuitarFiltros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerCategorias.setSelection(0); // Seleccionar "Todas"
                spinnerMarcas.setSelection(0); // Seleccionar "Todas"
                posicionCategoriaSeleccionada = 0;
                posicionMarcaSeleccionada = 0;
                obtenerProductosDesdeAPI();
                dialog.dismiss();
            }
        });

        // Mostrar el diálogo
        dialog.show();
    }
}


