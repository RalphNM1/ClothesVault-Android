package com.iesfernandowirtz.clothesvault;

import android.content.Context;
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
import com.iesfernandowirtz.clothesvault.modelo.modeloProducto;

import java.text.DecimalFormat;
import java.util.List;

public class AdaptadorProducto extends RecyclerView.Adapter<AdaptadorProducto.ProductoViewHolder> {

    public interface OnProductoClickListener {
        void onProductoClick(modeloProducto producto);
    }

    private OnProductoClickListener productoClickListener;

    public void setOnProductoClickListener(OnProductoClickListener listener) {
        this.productoClickListener = listener;
    }

    private Context context;
    private List<modeloProducto> productoList;

    public AdaptadorProducto(Context context, List<modeloProducto> productoList) {
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
        modeloProducto producto = productoList.get(position);
        holder.productoNombre.setText(producto.getNombre());
        DecimalFormat df = new DecimalFormat("#.00");
        String precioFormateado = df.format(producto.getPrecio());
        holder.productoPrecio.setText(precioFormateado + " €");
        holder.productoTalla.setText(producto.getTalla());

        String imagenBase64 = producto.getImagen();
        if (imagenBase64 != null && !imagenBase64.isEmpty()) {
            byte[] imagenBytes = Base64.decode(imagenBase64, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imagenBytes, 0, imagenBytes.length);

            Glide.with(context)
                    .load(bitmap)
                    .placeholder(R.drawable.imagen_test)
                    .error(R.drawable.imagen_test)
                    .into(holder.productoImagen);
        } else {
            holder.productoImagen.setImageResource(R.drawable.imagen_test);
        }

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
        TextView productoTalla;

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            productoImagen = itemView.findViewById(R.id.productoImagen);
            productoNombre = itemView.findViewById(R.id.productoNombre);
            productoPrecio = itemView.findViewById(R.id.productoPrecio);
            productoTalla = itemView.findViewById(R.id.productoTalla);
        }
    }
}