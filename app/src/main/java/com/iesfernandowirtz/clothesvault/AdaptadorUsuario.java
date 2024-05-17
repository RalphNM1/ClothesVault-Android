package com.iesfernandowirtz.clothesvault;

import android.content.Context;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import com.iesfernandowirtz.clothesvault.Modelo.Usuario;
import java.util.List;

public class AdaptadorUsuario extends ArrayAdapter<Usuario> {
    private Context context;
    private List<Usuario> usuarios;


    public AdaptadorUsuario(@NonNull Context context, int resource, @NonNull List<Usuario> objects) {
        super(context, resource, objects);
        this.context = context;
        this.usuarios = objects;
    }


}
