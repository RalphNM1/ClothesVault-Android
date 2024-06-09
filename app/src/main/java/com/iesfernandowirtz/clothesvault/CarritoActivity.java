package com.iesfernandowirtz.clothesvault;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.iesfernandowirtz.clothesvault.modelo.modeloDetallePedido;
import com.iesfernandowirtz.clothesvault.modelo.modeloProducto;
import com.iesfernandowirtz.clothesvault.utils.Apis;
import com.iesfernandowirtz.clothesvault.utils.ServicioDetallePedido;
import com.iesfernandowirtz.clothesvault.utils.ServicioPedido;
import com.iesfernandowirtz.clothesvault.utils.ServicioProducto;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarritoActivity extends ActividadBase implements AdaptadorCarrito.OnItemClickListener {


    private RecyclerView recyclerView;
    private AdaptadorCarrito adaptadorCarrito;
    private static TextView tvTotal;
    private ServicioProducto servicioProducto;
    private ServicioPedido servicioPedido;
    private ServicioDetallePedido servicioDetallePedido;

    private static boolean dialogoMostrado = false;

    private ImageView btAtras;

    private Button btnRealizarPedido;
    private String idPedido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);
        servicioProducto = Apis.getServicioProducto(this);
        servicioDetallePedido = Apis.getServicioDetallePedido(this);
        servicioPedido = Apis.getServicioPedido(this);

        recyclerView = findViewById(R.id.recyclerViewCarrito);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btAtras = findViewById(R.id.btAtras);
        tvTotal = findViewById(R.id.tvTotal);
        btnRealizarPedido = findViewById(R.id.btnRealizarPedido);


        adaptadorCarrito = new AdaptadorCarrito(new ArrayList<>(), new ArrayList<>());
        recyclerView.setAdapter(adaptadorCarrito);

        Intent intent = getIntent();
        idPedido = intent.getStringExtra("idPedido");

        actualizarTotal();

        adaptadorCarrito.setOnItemClickListener(this);

        if (idPedido != null) {
            // Llamar al método para cargar los detalles del pedido desde la API
            obtenerDetallesPedidoDesdeAPI(idPedido);
            actualizarTotal();
        } else {
            System.out.println("NO HAY PEDIDOS");

        }
        actualizarTotal();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorFondo));
        }
        btAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnRealizarPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pasar
            }
        });

    }


    private void obtenerDetallesPedidoDesdeAPI(String idPedido) {
        String idPedidoSinDecimales = idPedido.replaceAll("\\.\\d+", "");

        Call<List<modeloDetallePedido>> call = servicioDetallePedido.obtenerDetallesPedido(Long.valueOf(idPedidoSinDecimales));

        call.enqueue(new Callback<List<modeloDetallePedido>>() {
            @Override
            public void onResponse(Call<List<modeloDetallePedido>> call, Response<List<modeloDetallePedido>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<modeloDetallePedido> detallesPedido = response.body();
                    List<modeloProducto> productos = new ArrayList<>();
                    List<Integer> cantidades = new ArrayList<>();

                    for (modeloDetallePedido detalle : detallesPedido) {
                        productos.add(detalle.getProducto());
                        cantidades.add(detalle.getCantidad());
                    }

                    // Actualizar el carrito con los datos obtenidos de la API
                    Carrito.getInstance().actualizarProductos(productos);
                    Carrito.getInstance().actualizarCantidades(cantidades);

                    // Actualizar los datos en el adaptador
                    adaptadorCarrito.actualizarDetallesPedido(detallesPedido);
                    adaptadorCarrito.notifyDataSetChanged();

                    // Actualizar el total
                    actualizarTotal();
                } else {
                    Log.e("Error", "Error al obtener detalles del pedido");
                }
            }

            @Override
            public void onFailure(Call<List<modeloDetallePedido>> call, Throwable t) {
                Log.e("Error", "Error en la solicitud HTTP: " + t.getMessage());
            }
        });
    }


    private static void actualizarTotal() {
        double total = 0.0;
        List<modeloProducto> productos = Carrito.getInstance().getProductos();
        List<Integer> cantidades = Carrito.getInstance().getCantidades();

        if (productos.isEmpty()) {
            tvTotal.setText("Total: 0,00 €");
            return;
        }

        for (int i = 0; i < productos.size(); i++) {
            total += productos.get(i).getPrecio() * cantidades.get(i);
        }

        DecimalFormat df = new DecimalFormat("#.00");
        tvTotal.setText("Total: " + df.format(total) + " €");

    }


    @Override
    public void onItemClick(int position, int cantidad) {
        if (!dialogoMostrado) {
            modeloProducto producto = Carrito.getInstance().getProductos().get(position);
            mostrarDialogoProducto(producto, this, cantidad, position, adaptadorCarrito);
            dialogoMostrado = true;
        }
    }


    public void mostrarDialogoProducto(modeloProducto producto, Context context, int cantidadActual, int position, AdaptadorCarrito adaptadorCarrito) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_producto_comprar);
        modeloProducto productoEnCarrito = null;
        ImageView imageView = dialog.findViewById(R.id.productoImagen);
        TextView nombreTextView = dialog.findViewById(R.id.productoNombre);
        TextView descripcionTextView = dialog.findViewById(R.id.productoDesc);
        TextView precioTextView = dialog.findViewById(R.id.productoPrecio);
        TextView tallaTextView = dialog.findViewById(R.id.productoTalla);
        EditText cantidadEditText = dialog.findViewById(R.id.etCantidad);
        Button btnmas = dialog.findViewById(R.id.btnmas);
        Button btnmenos = dialog.findViewById(R.id.btnmenos);
        Button btnComprar = dialog.findViewById(R.id.btnComprar);

        String imagenBase64 = producto.getImagen();
        if (imagenBase64 != null && !imagenBase64.isEmpty()) {
            byte[] imagenBytes = Base64.decode(imagenBase64, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imagenBytes, 0, imagenBytes.length);
            Glide.with(context).load(bitmap).placeholder(R.drawable.imagen_test).error(R.drawable.imagen_test).into(imageView);
        } else {
            imageView.setImageResource(R.drawable.imagen_test);
        }

        nombreTextView.setText(producto.getNombre());
        descripcionTextView.setText(producto.getDescripcion());
        DecimalFormat df = new DecimalFormat("#.00");
        String precioFormateado = df.format(producto.getPrecio());
        precioTextView.setText("Precio: " + precioFormateado + " €");
        tallaTextView.setText("Talla: " + producto.getTalla());

        // Mostrar la cantidad actual en el EditText
        cantidadEditText.setText(String.valueOf(cantidadActual));

        btnmas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cantidadActual = Integer.parseInt(cantidadEditText.getText().toString());
                cantidadActual++;
                cantidadEditText.setText(String.valueOf(cantidadActual));
            }
        });

        btnmenos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cantidadActual = Integer.parseInt(cantidadEditText.getText().toString());
                if (cantidadActual > 0) {
                    cantidadActual--;
                    cantidadEditText.setText(String.valueOf(cantidadActual));
                }
            }
        });

        btnComprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idPedidoSinDecimales = idPedido.replaceAll("\\.\\d+", "");

                try {
                    int nuevaCantidad = Integer.parseInt(cantidadEditText.getText().toString());
                    Carrito carrito = Carrito.getInstance();

                    // Verificar que la posición es válida
                    if (position < 0 || position >= carrito.getProductos().size()) {
                        Utilidades.mostrarToastError(CarritoActivity.this, "Posición no válida");
                        dialog.dismiss();
                        return;
                    }

                    modeloProducto productoEnCarrito = carrito.getProductos().get(position); // Obtener el producto antes de realizar cualquier operación

                    if (nuevaCantidad > 0) {
                        // Actualizar la cantidad del producto en el carrito
                        carrito.actualizarCantidadProducto(productoEnCarrito, nuevaCantidad);

                        // Llamar al método para actualizar la cantidad en la base de datos
                        servicioPedido.actualizarCantidadEnBD(Long.parseLong(idPedidoSinDecimales), productoEnCarrito.getId(), nuevaCantidad).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    Log.d("CarritoActivity", "Cantidad actualizada en la base de datos");
                                    obtenerDetallesPedidoDesdeAPI(idPedido);
                                    cantidadEditText.setText(String.valueOf(nuevaCantidad));
                                } else {
                                    Log.e("CarritoActivity", "Error al actualizar la cantidad en la base de datos: " + response);
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Log.e("CarritoActivity", "Error en la solicitud HTTP: " + t.getMessage());
                            }
                        });
                    } else if (nuevaCantidad == 0) {
                        // Verificar que la posición es válida antes de eliminar
                        if (position >= 0 && position < carrito.getProductos().size()) {
                            // Eliminar el producto del carrito si la nueva cantidad es cero y la posición es válida
                            carrito.eliminarProducto(position);

                            // Llamar al método para eliminar el producto de la base de datos
                            servicioPedido.eliminarProductoDeBD(Long.parseLong(idPedidoSinDecimales), productoEnCarrito.getId()).enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    if (response.isSuccessful()) {
                                        obtenerDetallesPedidoDesdeAPI(idPedido);
                                        Log.d("CarritoActivity", "Producto eliminado de la base de datos");
                                    } else {
                                        Log.e("CarritoActivity", "Error al eliminar el producto de la base de datos");
                                    }
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    Log.e("CarritoActivity", "Error en la solicitud HTTP: " + t.getMessage());
                                }
                            });

                            adaptadorCarrito.notifyItemRemoved(position);
                            adaptadorCarrito.notifyItemRangeChanged(position, carrito.getProductos().size());
                        } else {
                            Log.e("CarritoActivity", "Índice fuera de rango al intentar eliminar un producto");
                        }
                    }
                    adaptadorCarrito.notifyDataSetChanged();
                    actualizarTotal();
                    dialog.dismiss();
                } catch (NumberFormatException e) {
                    Utilidades.mostrarToastError(CarritoActivity.this, "Formato de cantidad no válido");
                }
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                // Cuando el diálogo se cierra, establece la variable dialogoMostrado a false
                dialogoMostrado = false;
            }
        });
        dialog.show();
    }

}