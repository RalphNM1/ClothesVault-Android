package com.iesfernandowirtz.clothesvault.utils;

import com.iesfernandowirtz.clothesvault.modelo.Categoria;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ServicioCategoria {


    @GET("/categorias/listar")
    Call<List<Categoria>> getCategorias();

}
