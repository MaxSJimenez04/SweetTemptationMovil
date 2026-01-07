package com.example.sweettemptation.interfaces;

import com.example.sweettemptation.dto.PagoRequest;
import com.example.sweettemptation.dto.PagoResponse;
import com.example.sweettemptation.dto.PedidoDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PagoApi {
    @POST("pago/{idPedido}")
    Call<PagoResponse> realizarPago(
            @Path("idPedido") int idPedido,
            @Body PagoRequest request
    );
}
