// CarritoAdapter.java
package com.iesfernandowirtz.clothesvault;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.iesfernandowirtz.clothesvault.modelo.modeloDetallePedido;
import com.iesfernandowirtz.clothesvault.modelo.modeloProducto;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador para el RecyclerView que muestra los productos en el carrito de compras.
 */
public class AdaptadorCarrito extends RecyclerView.Adapter<AdaptadorCarrito.ViewHolder> {

    private List<modeloProducto> productos;
    private List<Integer> cantidades;
    private OnItemClickListener listener;
    private List<modeloDetallePedido> detallesPedido = new ArrayList<>();

    /**
     * Constructor del adaptador.
     * @param productos Lista de productos en el carrito.
     * @param cantidades Lista de cantidades correspondientes a los productos en el carrito.
     */
    public AdaptadorCarrito(List<modeloProducto> productos, List<Integer> cantidades) {
        this.productos = productos;
        this.cantidades = cantidades;
    }

    /**
     * Actualiza los detalles del pedido.
     * @param detallesPedido Lista de detalles del pedido.
     */
    public void actualizarDetallesPedido(List<modeloDetallePedido> detallesPedido) {
        this.detallesPedido.clear();
        this.detallesPedido.addAll(detallesPedido);
        this.productos.clear();
        this.cantidades.clear();
        for (modeloDetallePedido detalle : detallesPedido) {
            this.productos.add(detalle.getProducto());
            this.cantidades.add(detalle.getCantidad());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_carrito, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        modeloProducto producto = productos.get(position);
        int cantidad = cantidades.get(position);

        // Asigna el nombre del producto al TextView correspondiente.
        holder.nombreTextView.setText(producto.getNombre());
        // Asigna la cantidad del producto al TextView correspondiente.
        holder.cantidadTextView.setText("Cantidad: " + cantidad);

        // Calcula el precio total del producto.
        DecimalFormat df = new DecimalFormat("#.00");
        double precioTotal = producto.getPrecio() * cantidad;
        // Asigna el precio total del producto al TextView correspondiente.
        holder.precioTextView.setText("Precio: " + df.format(precioTotal) + " €");

        // Establece un listener para el item de la vista.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    int adapterPosition = holder.getAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        listener.onItemClick(adapterPosition, cantidades.get(adapterPosition));
                    }
                }
            }
        });

        // Carga la imagen del producto utilizando Glide.
        String imagenBase64 = producto.getImagen();
        if (imagenBase64 != null && !imagenBase64.isEmpty()) {
            byte[] imagenBytes = Base64.decode(imagenBase64, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imagenBytes, 0, imagenBytes.length);
            Glide.with(holder.itemView.getContext()).load(bitmap).into(holder.imageView);
        } else {
            // Si no hay imagen, muestra una imagen de prueba.
            holder.imageView.setImageResource(R.drawable.imagen_test);
        }
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    /**
     * Clase ViewHolder para mantener las vistas de cada elemento de la lista.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombreTextView;
        TextView cantidadTextView;
        TextView precioTextView;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Inicializa las vistas.
            nombreTextView = itemView.findViewById(R.id.productoNombre);
            cantidadTextView = itemView.findViewById(R.id.productoCantidad);
            precioTextView = itemView.findViewById(R.id.productoPrecio);
            imageView = itemView.findViewById(R.id.productoImagen);
        }
    }

    /**
     * Interfaz para manejar los eventos de clic en los elementos de la lista.
     */
    public interface OnItemClickListener {
        void onItemClick(int position, int cantidad);
    }

    /**
     * Establece el listener para los clics en los elementos de la lista.
     * @param listener El listener que manejará los eventos de clic.
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
