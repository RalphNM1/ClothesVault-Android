package com.iesfernandowirtz.clothesvault;

import android.content.Context;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import com.iesfernandowirtz.clothesvault.modelo.modeloUsuario;

import java.util.List;

public class AdaptadorUsuario extends ArrayAdapter<modeloUsuario> {
    private Context context;
    private List<modeloUsuario> usuarios;


    public AdaptadorUsuario(@NonNull Context context, int resource, @NonNull List<modeloUsuario> objects) {
        super(context, resource, objects);
        this.context = context;
        this.usuarios = objects;
    }


}
