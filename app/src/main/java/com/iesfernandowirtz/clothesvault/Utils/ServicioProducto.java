package com.iesfernandowirtz.clothesvault.Utils;


import com.iesfernandowirtz.clothesvault.Modelo.Categoria;
import com.iesfernandowirtz.clothesvault.Modelo.Producto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ServicioProducto {
    @GET("/productos/listar")
    Call<List<Producto>> getProducto();
    @GET("/productos/porCategoria/{idCategoria}")
    Call<List<Producto>> getProductosPorCategoria(@Path("idCategoria") Long idCategoria);

    @GET("/productos/categorias")
    Call<List<Categoria>> getCategorias();

    @GET("/productos/marcas")
    Call<List<String>> getMarcas();



}

