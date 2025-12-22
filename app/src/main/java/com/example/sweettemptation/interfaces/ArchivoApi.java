package com.example.sweettemptation.interfaces;

import com.example.sweettemptation.dto.ArchivoDTO;
import com.example.sweettemptation.dto.DetallesArchivoDTO;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ArchivoApi {
    @GET("archivo/detalle")
    Call<DetallesArchivoDTO> obtenerRutaArchivo(@Query("idProducto") int idProducto);

    @GET("archivo/{id}")
    Call<ArchivoDTO> obtenerImagen(@Path("id") int id);
}
