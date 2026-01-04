package com.example.sweettemptation.interfaces;

import com.example.sweettemptation.dto.ArchivoDTO;
import com.example.sweettemptation.dto.DetallesArchivoDTO;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ArchivoApi {

    // Coincide con GET /archivo/detalle?idProducto=X
    // En ArchivoApi.java
    @GET("archivo/detalle")
    Call<DetallesArchivoDTO> obtenerDetallesArchivo(@Query("idProducto") int idProducto);

    @GET("archivo/{id}")
    Call<ArchivoDTO> obtenerImagen(@Path("id") int id);
}
