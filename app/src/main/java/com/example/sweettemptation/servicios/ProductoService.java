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

    // --- LISTAR PRODUCTOS ---
    public void listarProductos(ResultCallback<List<ProductoDTO>> cb) {
        api.getProductos().enqueue(new Callback<List<ProductoDTO>>() {
            @Override
            public void onResponse(Call<List<ProductoDTO>> call, Response<List<ProductoDTO>> response) {
                if (response.isSuccessful()) {
                    cb.onResult(ApiResult.exito(response.body(), response.code()));
                } else {
                    manejarErroresEstandar(response.code(), response, cb);
                }
            }

            @Override
            public void onFailure(Call<List<ProductoDTO>> call, Throwable t) {
                manejarFallaConexion(t, cb);
            }
        });
    }

    // --- CREAR PRODUCTO (Ruta y Nombres sincronizados) ---
    public void crearProducto(ProductoDTO p, Uri uri, Context context, ResultCallback<Void> cb) {
        try {
            // 1. Convertimos el objeto Producto a un JSON (texto)
            Gson gson = new Gson();
            String json = gson.toJson(p);
            RequestBody bodyJson = RequestBody.create(MediaType.parse("application/json"), json);

            // 2. Preparamos la imagen
            File file = FileUtils.getFileFromUri(context, uri);
            if (file == null) {
                cb.onResult(ApiResult.fallo(400, "No se pudo obtener el archivo de la imagen"));
                return;
            }

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);

            // CAMBIO IMPORTANTE: El nombre debe ser "imagen" para coincidir con @RequestPart("imagen") en Spring
            MultipartBody.Part bodyImagen = MultipartBody.Part.createFormData("imagen", file.getName(), requestFile);

            // 3. Llamada a la API
            api.crearProducto(bodyJson, bodyImagen).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        cb.onResult(ApiResult.exito(null, response.code()));
                    } else {
                        manejarErroresEstandar(response.code(), response, cb);
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    manejarFallaConexion(t, cb);
                }
            });
        } catch (Exception e) {
            cb.onResult(ApiResult.fallo(400, "Error al procesar el archivo: " + e.getMessage()));
        }
    }

    // --- ELIMINAR PRODUCTO ---
    public void eliminarProducto(int idProducto, ResultCallback<Void> cb) {
        // Sincronizado con Call<String> de la interfaz ProductoApi
        api.eliminarProducto(idProducto).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    cb.onResult(ApiResult.exito(null, response.code()));
                } else {
                    manejarErroresEstandar(response.code(), response, cb);
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                manejarFallaConexion(t, cb);
            }
        });
    }

    // --- MANEJO DE ERRORES ---
    private void manejarErroresEstandar(int codigo, Response<?> response, ResultCallback<?> cb) {
        switch (codigo) {
            case 403:
                cb.onResult(ApiResult.fallo(403, Constantes.MENSAJE_NO_AUTORIZADO));
                break;
            case 404:
                cb.onResult(ApiResult.fallo(404, "Recurso no encontrado (Verifica la URL en ProductoApi)"));
                break;
            case 500:
                cb.onResult(ApiResult.fallo(500, Constantes.MENSAJE_FALLA_SERVIDOR));
                break;
            default:
                String mensaje = ValidacionesRespuesta.leerErrorBody(response.errorBody());
                if (mensaje == null || (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R && mensaje.isBlank()))
                    mensaje = "Error: " + codigo;
                cb.onResult(ApiResult.fallo(codigo, mensaje));
                break;
        }
    }

    private void manejarFallaConexion(Throwable t, ResultCallback<?> cb) {
        cb.onResult(ApiResult.fallo(500, "Error de red o conexión: " + t.getMessage()));
    }
}
