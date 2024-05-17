package com.iesfernandowirtz.clothesvault.Utils;


import com.iesfernandowirtz.clothesvault.Modelo.Producto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ServicioProducto {
    @GET("/productos/listar")
    Call<List<Producto>> getProducto();
}

