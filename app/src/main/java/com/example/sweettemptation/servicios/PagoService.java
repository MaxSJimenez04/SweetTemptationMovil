package com.example.sweettemptation.servicios;

import com.example.sweettemptation.dto.PagoRequest;
import com.example.sweettemptation.dto.PagoResponse;
import com.example.sweettemptation.interfaces.ApiResult;
import com.example.sweettemptation.interfaces.PagoApi;
import com.example.sweettemptation.network.ValidacionesRespuesta;
import com.example.sweettemptation.utils.Constantes;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PagoService {
    public interface ResultCallback<T> { void onResult(ApiResult<T> result); }
    private final PagoApi api;

    public PagoService(PagoApi api) { this.api = api; }

    public Call<PagoResponse> realizarPago(int idPedido, PagoRequest request, ResultCallback<PagoResponse> cb) {
        Call<PagoResponse> call = api.realizarPago(idPedido, request);
        call.enqueue(new Callback<PagoResponse>() {
            @Override
            public void onResponse(Call<PagoResponse> call, Response<PagoResponse> response) {
                if (response.isSuccessful()) {
                    cb.onResult(ApiResult.exito(response.body(), response.code()));
                    return;
                }
                int codigo = response.code();
                String mensaje = ValidacionesRespuesta.leerErrorBody(response.errorBody());
                cb.onResult(ApiResult.fallo(codigo, mensaje != null ? mensaje : "Error: " + codigo));
            }

            @Override
            public void onFailure(Call<PagoResponse> call, Throwable t) {
                if (ValidacionesRespuesta.esDesconexion(t)) {
                    cb.onResult(ApiResult.fallo(503, Constantes.MENSAJE_SIN_CONEXION));
                }
            }
        });
        return call;
    }
}