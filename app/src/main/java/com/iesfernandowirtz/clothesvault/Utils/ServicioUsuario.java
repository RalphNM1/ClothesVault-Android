package com.iesfernandowirtz.clothesvault.Utils;

import com.iesfernandowirtz.clothesvault.Modelo.Usuario;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ServicioUsuario {
    @GET("/usuarios/listar")
    Call<List<Usuario>> getUsuario();
}
