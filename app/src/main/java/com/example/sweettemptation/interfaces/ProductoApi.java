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

    // utiliza el nuevo metodo de la api
    @Multipart
    @POST("producto/nuevo-movil")
    Call<Void> crearProducto(
            @Part("producto") RequestBody producto,
            @Part MultipartBody.Part imagen
    );

    @Multipart
    @PUT("producto/{id}")
    Call<Void> actualizarProducto(
            @Path("id") int id,
            @Part("producto") RequestBody producto,
            @Part MultipartBody.Part imagen
    );

    @DELETE("producto/{id}")
    Call<String> eliminarProducto(@Path("id") int id);
}