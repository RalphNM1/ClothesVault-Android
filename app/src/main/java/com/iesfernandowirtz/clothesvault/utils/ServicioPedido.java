package com.iesfernandowirtz.clothesvault.utils;

import com.iesfernandowirtz.clothesvault.modelo.modeloDetallePedido;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ServicioPedido {

    @POST("/pedidos/{idPedido}/{idProducto}/actualizarCantidad")
    Call<Void> actualizarCantidadEnBD(@Path("idPedido") Long idPedido, @Path("idProducto") Long idProducto, @Query("nuevaCantidad") int nuevaCantidad);

    @DELETE("/pedidos/{idPedido}/{idProducto}/eliminarProducto")
    Call<Void> eliminarProductoDeBD(@Path("idPedido") Long idPedido, @Path("idProducto") Long idProducto);
}
