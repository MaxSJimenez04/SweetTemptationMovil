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

    // MODIFICADO: Ahora soporta imagen al crear
    @Multipart
    @POST("producto/nuevo")
    Call<Integer> crearProducto(
            @Part("producto") RequestBody productoJson, // Los datos del pastel en JSON
            @Part MultipartBody.Part imagen            // El archivo de la foto
    );

    @Multipart
    @PUT("producto/{id}")
    Call<ProductoDTO> actualizarProducto(
            @Path("id") int id,
            @Part("producto") RequestBody productoJson,
            @Part MultipartBody.Part imagen
    );

    @DELETE("producto/{id}")
    Call<String> eliminarProducto(@Path("id") int id);
}