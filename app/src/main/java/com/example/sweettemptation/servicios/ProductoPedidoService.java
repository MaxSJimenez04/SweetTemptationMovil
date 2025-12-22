package com.example.sweettemptation.servicios;

import com.example.sweettemptation.dto.DetallesProductoDTO;
import com.example.sweettemptation.dto.PedidoDTO;
import com.example.sweettemptation.dto.ProductoPedidoDTO;
import com.example.sweettemptation.interfaces.ApiResult;
import com.example.sweettemptation.interfaces.ProductoPedidoApi;
import com.example.sweettemptation.network.ValidacionesRespuesta;
import com.example.sweettemptation.utils.Constantes;

import java.math.BigDecimal;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductoPedidoService{
    public interface ResultCallBack<T>{
        void onResult(ApiResult<T> result);
    }

    private final ProductoPedidoApi api;

    public ProductoPedidoService(ProductoPedidoApi api){
        this.api = api;
    }

    public Call<ProductoPedidoDTO> crearProducto(int idProducto, int idPedido, int cantidad, ResultCallBack<ProductoPedidoDTO> cb){
        Call<ProductoPedidoDTO> call = api.crearProducto(idPedido, idProducto, idPedido, cantidad);
        call.enqueue(new Callback<ProductoPedidoDTO>() {
            @Override
            public void onResponse(Call<ProductoPedidoDTO> call, Response<ProductoPedidoDTO> response) {
                if(response.isSuccessful()){
                    cb.onResult(ApiResult.exito(response.body(), response.code()));
                    return;
                }

                int codigo = response.code();
                switch (codigo){
                    case 400:
                        cb.onResult(ApiResult.fallo(codigo, "ID de pedido o producto son inválidas"));
                        break;
                    case 403:
                        cb.onResult(ApiResult.fallo(codigo, Constantes.MENSAJE_NO_AUTORIZADO));
                        break;
                    case 404:
                        cb.onResult(ApiResult.fallo(codigo, "No se encontró el pedido o el producto deseado"));
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

            @Override
            public void onFailure(Call<ProductoPedidoDTO> call, Throwable t) {
                if(ValidacionesRespuesta.esDesconexion(t)){
                    cb.onResult(ApiResult.fallo(503, Constantes.MENSAJE_SIN_CONEXION));
                }
            }
        });
        return call;
    }

    public Call<ProductoPedidoDTO> actualizarProducto(int idPedido, ProductoPedidoDTO productoActualizado, ResultCallBack<ProductoPedidoDTO> cb){
        Call<ProductoPedidoDTO> call = api.actualizarProducto(idPedido, productoActualizado);
        call.enqueue(new Callback<ProductoPedidoDTO>() {
            @Override
            public void onResponse(Call<ProductoPedidoDTO> call, Response<ProductoPedidoDTO> response) {
                if (response.isSuccessful()){
                    cb.onResult(ApiResult.exito(response.body(), response.code()));
                }
                int codigo = response.code();

                switch (codigo){
                    case 400:
                        cb.onResult(ApiResult.fallo(codigo, "Se agregaron más productos de los existentes"));
                        break;
                    case 403:
                        cb.onResult(ApiResult.fallo(codigo, Constantes.MENSAJE_NO_AUTORIZADO));
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

            @Override
            public void onFailure(Call<ProductoPedidoDTO> call, Throwable t) {
                if (ValidacionesRespuesta.esDesconexion(t)){
                    cb.onResult(ApiResult.fallo(503, Constantes.MENSAJE_SIN_CONEXION));
                }
            }
        });
        return call;
    }

    public Call<PedidoDTO> recalcularTotal(int idPedido, BigDecimal total, ResultCallBack<PedidoDTO> cb){
        Call<PedidoDTO> call = api.recalcularTotal(idPedido,idPedido, total);
        call.enqueue(new Callback<PedidoDTO>() {
            @Override
            public void onResponse(Call<PedidoDTO> call, Response<PedidoDTO> response) {
                if(response.isSuccessful()){
                    cb.onResult(ApiResult.exito(response.body(), response.code()));
                    return;
                }

                int codigo = response.code();
                switch (codigo){
                    case 400:
                        cb.onResult(ApiResult.fallo(codigo, "El id del pedido es inválida"));
                        break;
                    case 403:
                        cb.onResult(ApiResult.fallo(codigo, Constantes.MENSAJE_NO_AUTORIZADO));
                        break;
                    case 404:
                        cb.onResult(ApiResult.fallo(codigo, "No se encontró el pedido"));
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

            @Override
            public void onFailure(Call<PedidoDTO> call, Throwable t) {
                if (ValidacionesRespuesta.esDesconexion(t)){
                    cb.onResult(ApiResult.fallo(503, Constantes.MENSAJE_SIN_CONEXION));
                }
            }
        });
        return call;
    }

    public Call<Void> eliminarProducto(int idPedido, int idProducto, ResultCallBack<Void> cb){
        Call<Void> call = api.eliminarProducto(idPedido, idProducto);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    cb.onResult(ApiResult.exito(response.body(), response.code()));
                    return;
                }

                int codigo = response.code();
                switch (codigo){
                    case 400:
                        cb.onResult(ApiResult.fallo(codigo, "El producto seleccionado no existe o ya ha sido eliminado"));
                        break;
                    case 403:
                        cb.onResult(ApiResult.fallo(codigo, Constantes.MENSAJE_NO_AUTORIZADO));
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

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (ValidacionesRespuesta.esDesconexion(t)){
                    cb.onResult(ApiResult.fallo(503, Constantes.MENSAJE_SIN_CONEXION));
                }
            }
        });

        return call;
    }

    public Call<List<DetallesProductoDTO>> consultarProductos(int idPedido, ResultCallBack<List<DetallesProductoDTO>> cb){
        Call<List<DetallesProductoDTO>> call = api.consultarProductos(idPedido, idPedido);
        call.enqueue(new Callback<List<DetallesProductoDTO>>() {
            @Override
            public void onResponse(Call<List<DetallesProductoDTO>> call, Response<List<DetallesProductoDTO>> response) {
                if(response.isSuccessful()){
                    cb.onResult(ApiResult.exito(response.body(), response.code()));
                    return;
                }

                int codigo = response.code();
                switch (codigo){
                    case 400:
                        cb.onResult(ApiResult.fallo(codigo, "El producto seleccionado no existe o ya ha sido eliminado"));
                        break;
                    case 403:
                        cb.onResult(ApiResult.fallo(codigo, Constantes.MENSAJE_NO_AUTORIZADO));
                        break;
                    case 404:
                        cb.onResult(ApiResult.fallo(codigo, "El pedido no existe"));
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

            @Override
            public void onFailure(Call<List<DetallesProductoDTO>> call, Throwable t) {
                if (ValidacionesRespuesta.esDesconexion(t)){
                    cb.onResult(ApiResult.fallo(503, Constantes.MENSAJE_SIN_CONEXION));
                }
            }
        });
        return call;
    }


    public Call<Void> comprarProductos(int idPedido, List<DetallesProductoDTO> productosAComprar, ResultCallBack<Void> cb){
        Call<Void> call = api.comprarProductos(idPedido, productosAComprar);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    cb.onResult(ApiResult.exito(response.body(), response.code()));
                    return;
                }

                int codigo = response.code();
                switch (codigo){
                    case 400:
                        cb.onResult(ApiResult.fallo(codigo, "El producto seleccionado no existe o ya ha sido eliminado"));
                        break;
                    case 403:
                        cb.onResult(ApiResult.fallo(codigo, Constantes.MENSAJE_NO_AUTORIZADO));
                        break;
                    default:
                        String mensaje = ValidacionesRespuesta.leerErrorBody(response.errorBody());
                        if (mensaje == null || mensaje.isBlank())
                            mensaje = "Error: " + codigo;
                        cb.onResult(ApiResult.fallo(codigo, mensaje));
                        break;
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if(ValidacionesRespuesta.esDesconexion(t)){
                    cb.onResult(ApiResult.fallo(503, Constantes.MENSAJE_SIN_CONEXION));
                }
            }
        });
        return call;
    }
}
