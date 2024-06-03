package com.iesfernandowirtz.clothesvault.Utils;


import com.iesfernandowirtz.clothesvault.Modelo.Categoria;
import com.iesfernandowirtz.clothesvault.Modelo.Producto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ServicioProducto {
    @GET("/productos/listar")
    Call<List<Producto>> getProducto();


    @GET("/productos/marcas")
    Call<List<String>> getMarcas();

    @GET("/productos/porCategoria")
    Call<List<Producto>> getProductosPorCategoria(@Query("idCategoria") Long idCategoria);

    @GET("/productos/porCategoria")
    Call<List<Producto>> getProductosPorCategoria(@Query("idCategoria") Long idCategoria, @Query("marca") String marca);
}

