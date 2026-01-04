package com.example.sweettemptation.interfaces;

import com.example.sweettemptation.dto.CategoriaDTO;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface CategoriaApi {
    // RUTA CORREGIDA: coincide con tu Controller de Spring Boot
    @GET("categoria/todos")
    Call<List<CategoriaDTO>> getCategorias();
}