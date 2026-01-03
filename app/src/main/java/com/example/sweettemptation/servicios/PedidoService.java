package com.example.sweettemptation.servicios;

import com.example.sweettemptation.dto.PedidoDTO;
import com.example.sweettemptation.interfaces.ApiResult;
import com.example.sweettemptation.interfaces.PedidoApi;
import com.example.sweettemptation.network.ValidacionesRespuesta;
import com.example.sweettemptation.utils.Constantes;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PedidoService {

   public interface ResultCallback<T>{
       void onResult(ApiResult<T> result);
   }

   private final PedidoApi api;

   public PedidoService(PedidoApi api){
       this.api = api;
   }

   public Call<PedidoDTO> obtenerPedidoActual(int idCliente, ResultCallback<PedidoDTO> cb){
       Call<PedidoDTO> call = api.obtenerPedidoActual(idCliente);
       call.enqueue(new Callback<PedidoDTO>() {
           @Override
           public void onResponse(Call<PedidoDTO> call, Response<PedidoDTO> response) {
               if (response.isSuccessful()){
                   cb.onResult(ApiResult.exito(response.body(), response.code()));
                   return;
               }

               int codigo = response.code();
               switch (codigo){
                   case 400:
                       cb.onResult(ApiResult.fallo(400, "ID del Cliente es inválida"));
                       break;
                   case 403:
                       cb.onResult(ApiResult.fallo(403, Constantes.MENSAJE_NO_AUTORIZADO));
                       break;
                   case 404:
                       cb.onResult(ApiResult.fallo(404, "No se encontró el cliente especificado"));
                       break;
                   default:
                       String mensaje;
                       try {
                           mensaje = ValidacionesRespuesta.leerErrorBody(response.errorBody());
                       } catch (Exception e) {
                           mensaje = null;
                       }
                       if (mensaje == null || mensaje.isBlank()) mensaje = "Error: " + codigo;
                       cb.onResult(ApiResult.fallo(codigo, mensaje));
                       break;
               }
           }

           @Override
           public void onFailure(Call<PedidoDTO> call, Throwable t) {
               String msg = t.getMessage();
               if (msg == null || msg.isBlank()) msg = t.getClass().getSimpleName();
               cb.onResult(ApiResult.fallo(503, Constantes.MENSAJE_SIN_CONEXION));
           }
       });
       return  call;
   }

   public Call<Void> crearPedido(int idCliente, ResultCallback<Void> cb){
       Call<Void> call = api.crearPedido(idCliente);
       call.enqueue(new Callback<Void>() {
           @Override
           public void onResponse(Call<Void> call, Response<Void> response) {
               if (response.isSuccessful()){
                   cb.onResult(ApiResult.exito(response.body(), response.code()));
                   return;
               }
               int codigo = response.code();

               switch (codigo){
                   case 400:
                       cb.onResult(ApiResult.fallo(codigo, "El ID del cliente es inválido"));
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

   public Call<Void> cancelarPedido(int idPedido, ResultCallback<Void> cb){
       Call<Void> call = api.cancelarPedido(idPedido);
       call.enqueue(new Callback<Void>() {
           @Override
           public void onResponse(Call<Void> call, Response<Void> response) {
               if (response.isSuccessful()){
                   cb.onResult(ApiResult.exito(response.body(), response.code()));
                   return;
               }
               int codigo = response.code();
               switch (codigo){
                   case 400:
                       cb.onResult(ApiResult.fallo(codigo, "El pedido no existe o ya se ha cancelado/pagado"));
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
           public void onFailure(Call<Void> call, Throwable t) {
               if (ValidacionesRespuesta.esDesconexion(t)){
                   cb.onResult(ApiResult.fallo(503, Constantes.MENSAJE_SIN_CONEXION));
               }
           }
       });
       return call;
   }


}
