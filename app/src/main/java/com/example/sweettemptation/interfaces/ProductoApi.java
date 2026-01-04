package com.example.sweettemptation.interfaces;

import com.example.sweettemptation.dto.ProductoDTO;
import java.util.List;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface ProductoApi {

    @GET("producto/todos")
    Call<List<ProductoDTO>> getProductos();

    @GET("producto/{id}")
    Call<ProductoDTO> getProducto(@Path("id") int id);

    /**
     * Registro de producto EXCLUSIVO para móvil.
     * Apuntamos a 'nuevo-movil' que es el método Multipart que agregamos al servidor.
     */
    @Multipart
    @POST("producto/nuevo-movil") // <-- Cambiado para usar el nuevo endpoint
    Call<Void> crearProducto(
            @Part("producto") RequestBody producto,
            @Part MultipartBody.Part imagen
    );

    /**
     * Actualización de producto.
     */
    @Multipart
    @PUT("producto/{id}")
    Call<Void> actualizarProducto(
            @Path("id") int id,
            @Part("producto") RequestBody producto,
            @Part MultipartBody.Part imagen
    );

    /**
     * Eliminación de producto.
     * Call<String> para recibir el mensaje de confirmación del servidor.
     */
    @DELETE("producto/{id}")
    Call<String> eliminarProducto(@Path("id") int id);
}