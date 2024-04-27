package com.iesfernandowirtz.clothesvault;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.iesfernandowirtz.clothesvault.Modelo.Producto;
import com.iesfernandowirtz.clothesvault.Modelo.Usuario;

import java.util.List;

public class AdaptadorProducto extends ArrayAdapter<Producto> {
    private Context context;
    private List<Producto> productos;
    public AdaptadorProducto(@NonNull Context context, int resource, @NonNull List<Producto> objects) {
        super(context, resource, objects);
        this.context = context;
        this.productos = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = layoutInflater.inflate(R.layout.content_main, parent, false);


        TextView txtNombre = (TextView) rowView.findViewById(R.id.Nombre);
        TextView txtDescripcion = (TextView) rowView.findViewById(R.id.Descripcion);
        TextView txtPrecio = (TextView) rowView.findViewById(R.id.Precio);

        txtNombre.setText("ID: " + productos.get(position).getNombre());
        txtDescripcion.setText("Nombre: " + productos.get(position).getDescripcion());
        txtPrecio.setText("Apellido: " + productos.get(position).getPrecio());

        return rowView;
    }

}
