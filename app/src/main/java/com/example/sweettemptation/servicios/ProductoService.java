package com.example.sweettemptation.servicios;

import android.content.Context;
import android.net.Uri;

import com.example.sweettemptation.dto.ProductoDTO;
import com.example.sweettemptation.interfaces.ApiResult;
import com.example.sweettemptation.interfaces.ProductoApi;
import com.example.sweettemptation.network.ValidacionesRespuesta;
import com.example.sweettemptation.utils.Constantes;
import com.example.sweettemptation.utils.FileUtils;
import com.google.gson.Gson;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductoService {

    public interface ResultCallback<T> {
        void onResult(ApiResult<T> result);
    }

    private final ProductoApi api;

    public ProductoService(ProductoApi api) {
        this.api = api;
    }

    // LISTAR TODOS LOS PRODUCTOS
    public void listarProductos(ResultCallback<List<ProductoDTO>> cb) {
        api.getProductos().enqueue(new Callback<List<ProductoDTO>>() {
            @Override
            public void onResponse(Call<List<ProductoDTO>> call, Response<List<ProductoDTO>> response) {
                if (response.isSuccessful()) {
                    cb.onResult(ApiResult.exito(response.body(), response.code()));
                    return;
                }
                manejarErroresEstandar(response.code(), response, cb);
            }

            @Override
            public void onFailure(Call<List<ProductoDTO>> call, Throwable t) {
                manejarFallaConexion(t, cb);
            }
        });
    }

    // CREAR NUEVO PRODUCTO CON IMAGEN (Multipart)
    // Este método sustituye al anterior para cumplir con la interfaz ProductoApi @Multipart
    public void crearProducto(ProductoDTO p, Uri uri, Context context, ResultCallback<Integer> cb) {
        try {
            // 1. Convertir el objeto Producto a JSON (Texto)
            Gson gson = new Gson();
            RequestBody bodyJson = RequestBody.create(
                    MediaType.parse("application/json"), gson.toJson(p));

            // 2. Convertir la URI de la galería a un archivo real usando tu utilidad FileUtils
            File file = FileUtils.getFileFromUri(context, uri);
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);

            // "foto" es el nombre del parámetro que espera tu @RequestPart en Spring Boot
            MultipartBody.Part bodyImagen = MultipartBody.Part.createFormData("foto", file.getName(), requestFile);

            // 3. Llamar a la API enviando ambas partes
            api.crearProducto(bodyJson, bodyImagen).enqueue(new Callback<Integer>() {
                @Override
                public void onResponse(Call<Integer> call, Response<Integer> response) {
                    if (response.isSuccessful()) {
                        cb.onResult(ApiResult.exito(response.body(), response.code()));
                    } else {
                        manejarErroresEstandar(response.code(), response, cb);
                    }
                }

                @Override
                public void onFailure(Call<Integer> call, Throwable t) {
                    manejarFallaConexion(t, cb);
                }
            });
        } catch (Exception e) {
            cb.onResult(ApiResult.fallo(400, "No se pudo procesar la imagen: " + e.getMessage()));
        }
    }

    // ELIMINAR PRODUCTO
    public void eliminarProducto(int idProducto, ResultCallback<String> cb) {
        api.eliminarProducto(idProducto).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    cb.onResult(ApiResult.exito(response.body(), response.code()));
                    return;
                }
                manejarErroresEstandar(response.code(), response, cb);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                manejarFallaConexion(t, cb);
            }
        });
    }

    // --- MÉTODOS DE APOYO PARA ERRORES ---

    private void manejarErroresEstandar(int codigo, Response<?> response, ResultCallback<?> cb) {
        switch (codigo) {
            case 403:
                cb.onResult(ApiResult.fallo(403, Constantes.MENSAJE_NO_AUTORIZADO));
                break;
            case 404:
                cb.onResult(ApiResult.fallo(404, "Recurso no encontrado"));
                break;
            case 500:
                cb.onResult(ApiResult.fallo(500, Constantes.MENSAJE_FALLA_SERVIDOR));
                break;
            default:
                String mensaje = ValidacionesRespuesta.leerErrorBody(response.errorBody());
                if (mensaje == null || mensaje.isBlank()) mensaje = "Error inesperado: " + codigo;
                cb.onResult(ApiResult.fallo(codigo, mensaje));
                break;
        }
    }

    private void manejarFallaConexion(Throwable t, ResultCallback<?> cb) {
        if (ValidacionesRespuesta.esDesconexion(t)) {
            cb.onResult(ApiResult.fallo(503, Constantes.MENSAJE_SIN_CONEXION));
        } else {
            cb.onResult(ApiResult.fallo(500, "Error de red: " + t.getMessage()));
        }
    }
}
