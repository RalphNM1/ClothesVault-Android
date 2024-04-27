package com.iesfernandowirtz.clothesvault;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.iesfernandowirtz.clothesvault.Modelo.Usuario;

import org.w3c.dom.Text;

import java.util.List;

public class AdaptadorUsuario extends ArrayAdapter<Usuario> {
    private Context context;
    private List<Usuario> usuarios;


    public AdaptadorUsuario(@NonNull Context context, int resource, @NonNull List<Usuario> objects) {
        super(context, resource, objects);
        this.context = context;
        this.usuarios = objects;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = layoutInflater.inflate(R.layout.content_main, parent, false);


     /*   TextView txtidUsuario = (TextView) rowView.findViewById(R.id.ID);
        TextView txtNombre = (TextView) rowView.findViewById(R.id.Nombre);
        TextView txtApellido = (TextView) rowView.findViewById(R.id.Apellido);

        txtidUsuario.setText("ID: " + usuarios.get(position).getId());
        txtNombre.setText("Nombre: " + usuarios.get(position).getNombre());
        txtApellido.setText("Apellido: " + usuarios.get(position).getApellido1());
*/
        return rowView;
    }

}
