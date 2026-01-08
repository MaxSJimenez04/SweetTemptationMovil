package com.example.sweettemptation.servicios;

import com.example.sweettemptation.dto.EstadisticaProductoDTO;
import com.example.sweettemptation.dto.EstadisticaVentaProductoDTO;
import com.example.sweettemptation.dto.PedidoDTO;
import com.example.sweettemptation.interfaces.ApiResult;
import com.example.sweettemptation.interfaces.EstadisticasApi;
import com.example.sweettemptation.network.ValidacionesRespuesta;
import com.example.sweettemptation.utils.Constantes;

import java.time.LocalDate;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EstadisticasService {

    public interface ResultCallback<T> {
        void onResult(ApiResult<T> result);
    }

    private final EstadisticasApi api;

    public EstadisticasService(EstadisticasApi api) {
        this.api = api;
    }

    public Call<List<PedidoDTO>> consultarVentas(String fechaInicio, String fechaFin, String estado,
                                                 ResultCallback<List<PedidoDTO>> cb) {
        Call<List<PedidoDTO>> call = api.consultarVentas(fechaInicio, fechaFin, estado);
        call.enqueue(new Callback<List<PedidoDTO>>() {
            @Override
            public void onResponse(Call<List<PedidoDTO>> call, Response<List<PedidoDTO>> response) {
                if (response.isSuccessful()) {
                    cb.onResult(ApiResult.exito(response.body(), response.code()));
                    return;
                }
                manejarErroresEstandar(response, cb);
            }

            @Override
            public void onFailure(Call<List<PedidoDTO>> call, Throwable t) {
                manejarFallaConexion(t, cb);
            }
        });
        return call;
    }

    public Call<List<EstadisticaProductoDTO>> obtenerEstadisticasProductos(LocalDate fechaInicio, LocalDate fechaFin,
                                                                           ResultCallback<List<EstadisticaProductoDTO>> cb) {
        Call<List<EstadisticaProductoDTO>> call = api.obtenerEstadisticasProductos(fechaInicio, fechaFin);
        call.enqueue(new Callback<List<EstadisticaProductoDTO>>() {
            @Override
            public void onResponse(Call<List<EstadisticaProductoDTO>> call, Response<List<EstadisticaProductoDTO>> response) {
                if (response.isSuccessful()) {
                    cb.onResult(ApiResult.exito(response.body(), response.code()));
                    return;
                }
                manejarErroresEstandar(response, cb);
            }

            @Override
            public void onFailure(Call<List<EstadisticaProductoDTO>> call, Throwable t) {
                manejarFallaConexion(t, cb);
            }
        });
        return call;
    }

    public Call<List<EstadisticaVentaProductoDTO>> obtenerVentaProducto(int idProducto,
                                                                        String fechaInicio, String fechaFin,
                                                                        ResultCallback<List<EstadisticaVentaProductoDTO>> cb) {
        Call<List<EstadisticaVentaProductoDTO>> call = api.obtenerVentasProducto(idProducto, fechaInicio, fechaFin, idProducto);
        call.enqueue(new Callback<List<EstadisticaVentaProductoDTO>>() {
            @Override
            public void onResponse(Call<List<EstadisticaVentaProductoDTO>> call, Response<List<EstadisticaVentaProductoDTO>> response) {
                if (response.isSuccessful()) {
                    cb.onResult(ApiResult.exito(response.body(), response.code()));
                    return;
                }
                manejarErroresEstandar(response, cb);
            }

            @Override
            public void onFailure(Call<List<EstadisticaVentaProductoDTO>> call, Throwable t) {
                manejarFallaConexion(t, cb);
            }
        });
        return call;
    }

    private <T> void manejarErroresEstandar(Response<T> response, ResultCallback<T> cb) {
        int codigo = response.code();
        switch (codigo) {
            case 400:
                cb.onResult(ApiResult.fallo(400, "La solicitud es inválida (Bad Request)"));
                break;
            case 403:
                cb.onResult(ApiResult.fallo(403, Constantes.MENSAJE_NO_AUTORIZADO));
                break;
            case 404:
                cb.onResult(ApiResult.fallo(404, "No se encontraron datos en el rango seleccionado"));
                break;
            case 500:
                cb.onResult(ApiResult.fallo(codigo, Constantes.MENSAJE_FALLA_SERVIDOR));
                break;
            default:
                String mensaje = ValidacionesRespuesta.leerErrorBody(response.errorBody());
                if (mensaje == null || mensaje.isBlank())
                    mensaje = "Error: " + codigo;
                cb.onResult(ApiResult.fallo(codigo, mensaje));
                break;
        }
    }

    private <T> void manejarFallaConexion(Throwable t, ResultCallback<T> cb) {
        if (ValidacionesRespuesta.esDesconexion(t)) {
            cb.onResult(ApiResult.fallo(503, Constantes.MENSAJE_SIN_CONEXION));
        } else {
            cb.onResult(ApiResult.fallo(500, "Error de red: " + t.getMessage()));
        }
    }
}
