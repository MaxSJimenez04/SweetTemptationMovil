package com.example.sweettemptation.interfaces;

import com.example.sweettemptation.dto.PedidoDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface PedidoApi {

    @GET("pedido/actual")
    Call<PedidoDTO> obtenerPedidoActual(@Query("idCliente") int idCliente);

    @POST("pedido/nuevo")
    Call<Void> crearPedido(@Query("idCliente") int idCliente);

    @PUT("pedido/")
    Call<Void> cancelarPedido(@Query("idPedido") int idPedido);
}
