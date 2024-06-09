package com.iesfernandowirtz.clothesvault.utils;

import com.iesfernandowirtz.clothesvault.modelo.modeloDetallePedido;
import com.iesfernandowirtz.clothesvault.modelo.modeloUsuario;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ServicioUsuario {

        @GET("/usuarios/listar")
        Call<List<modeloUsuario>> getUsuario();

        @GET("/usuarios/listarXEmail/{email}")
        Call<List<modeloUsuario>> getUsuarioXEmail(@Path("email") String email);

        @POST("/usuarios/insertar")
        Call<modeloUsuario> addUsuario(@Body modeloUsuario usuario);

        @POST("/usuarios/crearPedidos")
        Call<Map<String, Object>> crearPedidos(@Body modeloUsuario usuario);
    }
