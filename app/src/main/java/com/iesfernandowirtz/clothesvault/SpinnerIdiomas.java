package com.iesfernandowirtz.clothesvault;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

// Clase para personalizar un Spinner (desplegable) con imágenes y texto
public class SpinnerIdiomas extends ArrayAdapter<String> {
    private final Context mContext; // Contexto de la aplicación
    private final String[] mIdiomas; // Array de nombres de idiomas
    private final int[] mImagenes; // Array de IDs de imágenes correspondientes a los idiomas

    // Constructor de la clase
    public SpinnerIdiomas(Context context, int resource, String[] idiomas, int[] imagenes) {
        super(context, resource, idiomas); // Llama al constructor de la clase ArrayAdapter
        mContext = context; // Asigna el contexto
        mIdiomas = idiomas; // Asigna el array de idiomas
        mImagenes = imagenes; // Asigna el array de IDs de imágenes
    }

    // Sobrescribe el método getView para personalizar la vista del elemento seleccionado
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    // Sobrescribe el método getDropDownView para personalizar la vista de cada elemento en la lista desplegable
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    // Método privado para inicializar la vista de cada elemento
    private View initView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext); // Inicializa el LayoutInflater con el contexto
        View rowView = inflater.inflate(R.layout.idioma_row, parent, false); // Infla el diseño de fila personalizado

        // Obtiene referencias a los elementos de la fila
        ImageView imageView = rowView.findViewById(R.id.icon); // ImageView para la imagen del idioma
        TextView textView = rowView.findViewById(R.id.idioma); // TextView para el nombre del idioma

        // Asigna la imagen y el texto correspondientes al elemento en esta posición
        imageView.setImageResource(mImagenes[position]); // Establece la imagen del idioma
        textView.setText(mIdiomas[position]); // Establece el nombre del idioma

        return rowView; // Devuelve la vista personalizada
    }
}
