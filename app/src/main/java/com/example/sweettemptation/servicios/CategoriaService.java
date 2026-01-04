package com.example.sweettemptation.servicios;

import com.example.sweettemptation.dto.CategoriaDTO;
import com.example.sweettemptation.interfaces.ApiResult;
import com.example.sweettemptation.interfaces.CategoriaApi;
import com.example.sweettemptation.network.ValidacionesRespuesta;
import com.example.sweettemptation.utils.Constantes;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoriaService {

    private final CategoriaApi api;

    public CategoriaService(CategoriaApi api) {
        this.api = api;
    }

    public void listarCategorias(ProductoService.ResultCallback<List<CategoriaDTO>> cb) {
        Call<List<CategoriaDTO>> call = api.getCategorias();
        call.enqueue(new Callback<List<CategoriaDTO>>() {
            @Override
            public void onResponse(Call<List<CategoriaDTO>> call, Response<List<CategoriaDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cb.onResult(ApiResult.exito(response.body(), response.code()));
                } else {
                    int codigo = response.code();
                    String mensaje = ValidacionesRespuesta.leerErrorBody(response.errorBody());
                    cb.onResult(ApiResult.fallo(codigo, mensaje != null ? mensaje : "Error al cargar categorías"));
                }
            }

            @Override
            public void onFailure(Call<List<CategoriaDTO>> call, Throwable t) {
                if (ValidacionesRespuesta.esDesconexion(t)) {
                    cb.onResult(ApiResult.fallo(503, Constantes.MENSAJE_SIN_CONEXION));
                } else {
                    cb.onResult(ApiResult.fallo(500, t.getMessage()));
                }
            }
        });
    }
}