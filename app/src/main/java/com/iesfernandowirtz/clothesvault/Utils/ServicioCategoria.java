package com.iesfernandowirtz.clothesvault.Utils;

import com.iesfernandowirtz.clothesvault.Modelo.Categoria;
import com.iesfernandowirtz.clothesvault.Modelo.Producto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ServicioCategoria {


    @GET("/categorias/listar")
    Call<List<Categoria>> getCategorias();

    @GET("/categorias/listar2")
    Call<List<Categoria>> getCategorias2();
}
