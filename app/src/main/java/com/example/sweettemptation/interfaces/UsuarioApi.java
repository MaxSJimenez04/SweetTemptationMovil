package com.example.sweettemptation.interfaces;

import com.example.sweettemptation.dto.UsuarioRequest;
import com.example.sweettemptation.dto.UsuarioResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UsuarioApi {

    @GET("api/usuarios")
    Call<List<UsuarioResponse>> getUsuarios();

    @GET("api/usuarios/{id}")
    Call<UsuarioResponse> getUsuarioById(@Path("id") int id);

    @POST("api/usuarios")
    Call<UsuarioResponse> createUsuario(@Body UsuarioRequest request);

    @PUT("api/usuarios/{id}")
    Call<UsuarioResponse> updateUsuario(@Path("id") int id, @Body UsuarioRequest request);

    @DELETE("api/usuarios/{id}")
    Call<Void> deleteUsuario(@Path("id") int id);
}



