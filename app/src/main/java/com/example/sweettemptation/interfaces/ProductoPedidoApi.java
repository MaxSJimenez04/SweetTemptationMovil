package com.example.sweettemptation.interfaces;

import com.example.sweettemptation.dto.DetallesProductoDTO;
import com.example.sweettemptation.dto.PedidoDTO;
import com.example.sweettemptation.dto.ProductoPedidoDTO;

import java.math.BigDecimal;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ProductoPedidoApi {
    @POST("pedido/{id}/")
    Call<ProductoPedidoDTO> crearProducto(@Path("id") int id, @Query("idProducto") int idProducto,
    @Query("idPedido")int idPedido, @Query("cantidad") int cantidad);

    @PUT("pedido/{id}/")
    Call<ProductoPedidoDTO> actualizarProducto(@Path("id") int id, @Body ProductoPedidoDTO productoActualizado);

    @PUT("pedido/{id}/recalcular")
    Call<PedidoDTO> recalcularTotal(@Path("id") int id, @Query("idPedido") int idPedido, @Body BigDecimal cantidad);

    @DELETE("pedido/{id}/")
    Call<Void> eliminarProducto(@Path("id") int id, @Query("idProducto") int idProducto);

    @GET("pedido/{id}/")
    Call<List<DetallesProductoDTO>> consultarProductos(@Path("id") int id, @Query("idPedido") int idPedido);

    @PUT("pedido/{id}/comprar")
    Call<Void> comprarProductos(@Path("id") int id, @Body List<DetallesProductoDTO> productos);
}
