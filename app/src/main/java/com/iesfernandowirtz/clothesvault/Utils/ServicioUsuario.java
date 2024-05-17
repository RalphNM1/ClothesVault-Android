package com.iesfernandowirtz.clothesvault.Utils;

import com.iesfernandowirtz.clothesvault.Modelo.Usuario;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ServicioUsuario {
    @GET("/usuarios/listar")
    Call<List<Usuario>> getUsuario();

    @GET("/usuarios/listarXEmail/{email}")
    Call<List<Usuario>> getUsuarioXEmail(@Path("email") String email);


    @POST("/usuarios/insertar")
    Call<Usuario>addUsuario(@Body Usuario usuario);
}
