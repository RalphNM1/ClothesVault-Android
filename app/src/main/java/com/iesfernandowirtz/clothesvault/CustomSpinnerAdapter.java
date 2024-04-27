package com.iesfernandowirtz.clothesvault;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomSpinnerAdapter extends ArrayAdapter<String> {
    private final Context mContext;
    private final String[] mIdiomas;
    private final int[] mImagenes; // Array de IDs de imágenes

    public CustomSpinnerAdapter(Context context, int resource, String[] idiomas, int[] imagenes) {
        super(context, resource, idiomas);
        mContext = context;
        mIdiomas = idiomas;
        mImagenes = imagenes;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View rowView = inflater.inflate(R.layout.row, parent, false);

        ImageView imageView = rowView.findViewById(R.id.icon);
        TextView textView = rowView.findViewById(R.id.idioma);

        imageView.setImageResource(mImagenes[position]);
        textView.setText(mIdiomas[position]);

        return rowView;
    }
}
