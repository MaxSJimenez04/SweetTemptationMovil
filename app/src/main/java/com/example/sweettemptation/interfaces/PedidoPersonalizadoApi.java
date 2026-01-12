package com.example.sweettemptation.interfaces;

import com.example.sweettemptation.dto.SolicitudPersonalizadaDTO;
import com.example.sweettemptation.model.PedidoPersonalizado;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PedidoPersonalizadoApi {
    @POST("api/pedidos-personalizados")
    Call<PedidoPersonalizado> crearPedido(@Body SolicitudPersonalizadaDTO request);
}