package com.iesfernandowirtz.clothesvault.Utils;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.iesfernandowirtz.clothesvault.Modelo.Categoria;

import java.io.IOException;

public class CategoriaTypeAdapter extends TypeAdapter<Categoria> {

    @Override
    public void write(JsonWriter out, Categoria categoria) throws IOException {
        // Método no necesario para la deserialización
    }

    @Override
    public Categoria read(JsonReader in) throws IOException {
        Categoria categoria = new Categoria();
        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "id":
                    categoria.setId(in.nextLong());
                    break;
                case "nombre":
                    categoria.setNombre(in.nextString());
                    break;
                default:
                    in.skipValue(); // Ignorar otros campos no necesarios
                    break;
            }
        }
        in.endObject();
        return categoria;
    }
}
