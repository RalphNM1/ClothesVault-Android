package com.iesfernandowirtz.clothesvault;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.iesfernandowirtz.clothesvault.Modelo.Producto;

import java.text.DecimalFormat;
import java.util.List;
public class AdaptadorProducto extends RecyclerView.Adapter<AdaptadorProducto.ProductoViewHolder> {


    // Interfaz para manejar el clic del producto
    public interface OnProductoClickListener {
        void onProductoClick(Producto producto);
    }

    private OnProductoClickListener productoClickListener;

    // Método para establecer el oyente de clic del producto
    public void setOnProductoClickListener(OnProductoClickListener listener) {
        this.productoClickListener = listener;
    }

    private Context context;
    private List<Producto> productoList;

    public AdaptadorProducto(Context context, List<Producto> productoList) {
        this.context = context;
        this.productoList = productoList;
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_producto, parent, false);
        return new ProductoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        Producto producto = productoList.get(position);
        holder.productoNombre.setText(producto.getNombre());
        DecimalFormat df = new DecimalFormat("#.00"); // Formato para dos decimales
        String precioFormateado = df.format(producto.getPrecio());
        holder.productoPrecio.setText(precioFormateado + " €");
        holder.productoTalla.setText(producto.getTalla()); // Mostrar la talla

        // Cargar la imagen desde la URL usando Glide
        Glide.with(context)
                .load(producto.getImagenUrl()) // URL de la imagen
                .placeholder(R.drawable.imagen_test) // Imagen de carga mientras se descarga la imagen
                .error(R.drawable.imagen_test) // Imagen de error si la carga falla
                .into(holder.productoImagen); // ImageView donde se mostrará la imagen

        // Establecer el clic en el elemento de la lista
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (productoClickListener != null) {
                    productoClickListener.onProductoClick(producto);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return productoList.size();
    }

    public class ProductoViewHolder extends RecyclerView.ViewHolder {

        ImageView productoImagen;
        TextView productoNombre;
        TextView productoPrecio;
        TextView productoTalla; // Nuevo TextView para la talla

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            productoImagen = itemView.findViewById(R.id.productoImagen);
            productoNombre = itemView.findViewById(R.id.productoNombre);
            productoPrecio = itemView.findViewById(R.id.productoPrecio);
            productoTalla = itemView.findViewById(R.id.productoTalla); // Vincular el nuevo TextView
        }
    }
}
