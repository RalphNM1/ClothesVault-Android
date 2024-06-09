package com.iesfernandowirtz.clothesvault.utils;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iesfernandowirtz.clothesvault.modelo.modeloProducto;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ServicioProducto {
        @GET("/productos/listar")
        Call<List<modeloProducto>> getProducto();

        @GET("/productos/marcas")
        Call<List<String>> getMarcas();

        @GET("/productos/porCategoria")
        Call<List<modeloProducto>> getProductosPorCategoria(@Query("idCategoria") Long idCategoria);

        @GET("/productos/porCategoria")
        Call<List<modeloProducto>> getProductosPorCategoria(@Query("idCategoria") Long idCategoria, @Query("nombreProveedor") String nombreProveedor);

        @POST("/productos/agregarProducto")
        Call<Void> agregarProductoAlCarrito(@Body Map<String, Long> body, @Query("cantidad") int cantidad);
}
