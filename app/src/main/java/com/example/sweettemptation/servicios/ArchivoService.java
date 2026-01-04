package com.example.sweettemptation.servicios;

import com.example.sweettemptation.dto.ArchivoDTO;
import com.example.sweettemptation.dto.DetallesArchivoDTO;
import com.example.sweettemptation.interfaces.ApiResult;
import com.example.sweettemptation.interfaces.ArchivoApi;
import com.example.sweettemptation.network.ValidacionesRespuesta;
import com.example.sweettemptation.utils.Constantes;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArchivoService {
    public interface ResultCallback<T>{
        void onResult(ApiResult<T> result);
    }

    private final ArchivoApi api;

    public ArchivoService(ArchivoApi api){
        this.api = api;
    }

    public Call<DetallesArchivoDTO> obtenerDetallesArchivo(int idProducto, ResultCallback<DetallesArchivoDTO> cb){
        Call<DetallesArchivoDTO> call = api.obtenerDetallesArchivo(idProducto);
        call.enqueue(new Callback<DetallesArchivoDTO>() {
            @Override
            public void onResponse(Call<DetallesArchivoDTO> call, Response<DetallesArchivoDTO> response) {
                if (response.isSuccessful()){
                    cb.onResult(ApiResult.exito(response.body(), response.code()));
                    return;
                }

                int codigo = response.code();
                switch (codigo){
                    case 400:
                        cb.onResult(ApiResult.fallo(codigo, "Los datos de búsqueda son inválidos"));
                        break;
                    case 403:
                        cb.onResult(ApiResult.fallo(codigo, Constantes.MENSAJE_NO_AUTORIZADO));
                        break;
                    case 404:
                        cb.onResult(ApiResult.fallo(codigo, "Este pastel aún no tiene una foto asignada"));
                        break;
                    default:
                        manejarErroresEstandar(codigo, response, cb);
                        break;
                }
            }

            @Override
            public void onFailure(Call<DetallesArchivoDTO> call, Throwable t) {
                manejarFallaConexion(t, cb);
            }
        });
        return call;
    }

    public Call<ArchivoDTO> obtenerImagen(int idArchivo, ResultCallback<ArchivoDTO> cb){
        Call<ArchivoDTO> call = api.obtenerImagen(idArchivo);
        call.enqueue(new Callback<ArchivoDTO>() {
            @Override
            public void onResponse(Call<ArchivoDTO> call, Response<ArchivoDTO> response) {
                if (response.isSuccessful()){
                    cb.onResult(ApiResult.exito(response.body(), response.code()));
                    return;
                }

                int codigo = response.code();
                if (codigo == 404) {
                    cb.onResult(ApiResult.fallo(codigo, "No se encontró el archivo de imagen"));
                } else {
                    manejarErroresEstandar(codigo, response, cb);
                }
            }

            @Override
            public void onFailure(Call<ArchivoDTO> call, Throwable t) {
                manejarFallaConexion(t, cb);
            }
        });
        return call;
    }

    private void manejarErroresEstandar(int codigo, Response<?> response, ResultCallback<?> cb) {
        if (codigo == 500) {
            cb.onResult(ApiResult.fallo(codigo, Constantes.MENSAJE_FALLA_SERVIDOR));
        } else {
            String mensaje = ValidacionesRespuesta.leerErrorBody(response.errorBody());
            if (mensaje == null || mensaje.isBlank()) mensaje = "Error: " + codigo;
            cb.onResult(ApiResult.fallo(codigo, mensaje));
        }
    }

    private void manejarFallaConexion(Throwable t, ResultCallback<?> cb) {
        if (ValidacionesRespuesta.esDesconexion(t)) {
            cb.onResult(ApiResult.fallo(503, Constantes.MENSAJE_SIN_CONEXION));
        } else {
            cb.onResult(ApiResult.fallo(500, "Error de red: " + t.getMessage()));
        }
    }

    public Call<DetallesArchivoDTO> obtenerRutaArchivo(int idProducto, ResultCallback<DetallesArchivoDTO> cb) {
        return obtenerDetallesArchivo(idProducto, cb);
    }
}
