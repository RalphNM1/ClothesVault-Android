package com.iesfernandowirtz.clothesvault.utils;

import com.iesfernandowirtz.clothesvault.modelo.modeloDetallePedido;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ServicioDetallePedido {
    @GET("/detallepedidos/obtenerDetallesPedido")
    Call<List<modeloDetallePedido>> obtenerDetallesPedido(@Query("idPedido") Long idPedido);
}
