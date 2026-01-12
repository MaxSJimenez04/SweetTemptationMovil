package com.example.sweettemptation.servicios;

import com.example.sweettemptation.dto.SolicitudPersonalizadaDTO;
import com.example.sweettemptation.interfaces.ApiResult;
import com.example.sweettemptation.interfaces.PedidoPersonalizadoApi;
import com.example.sweettemptation.model.PedidoPersonalizado;
import com.example.sweettemptation.network.ValidacionesRespuesta;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PedidoCustomService {

    public interface ResultCallback<T> {
        void onResult(ApiResult<T> result);
    }

    private final PedidoPersonalizadoApi api;

    public PedidoCustomService(PedidoPersonalizadoApi api) {
        this.api = api;
    }

    public void enviarSolicitud(SolicitudPersonalizadaDTO request, ResultCallback<PedidoPersonalizado> cb) {
        api.crearPedido(request).enqueue(new Callback<PedidoPersonalizado>() {
            @Override
            public void onResponse(Call<PedidoPersonalizado> call, Response<PedidoPersonalizado> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cb.onResult(ApiResult.exito(response.body(), response.code()));
                } else {
                    String error = ValidacionesRespuesta.leerErrorBody(response.errorBody());
                    cb.onResult(ApiResult.fallo(response.code(), error != null ? error : "Error al enviar la solicitud"));
                }
            }

            @Override
            public void onFailure(Call<PedidoPersonalizado> call, Throwable t) {
                cb.onResult(ApiResult.fallo(503, "Sin conexión con el servidor"));
            }
        });
    }
}