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
import java.io.IOException;
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

    public void crearProducto(ProductoDTO p, Uri uri, Context context, ResultCallback<Void> cb) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(p);
            RequestBody bodyJson = RequestBody.create(MediaType.parse("application/json"), json);

            File file = FileUtils.getFileFromUri(context, uri);
            if (file == null) {
                cb.onResult(ApiResult.fallo(400, "No se pudo obtener el archivo de la imagen"));
                return;
            }

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);

            MultipartBody.Part bodyImagen = MultipartBody.Part.createFormData("imagen", file.getName(), requestFile);

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

    public void eliminarProducto(int idProducto, ResultCallback<Void> cb) {
        api.eliminarProducto(idProducto).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                // forzar mensaje de exito
                if (response.code() == 200 || response.isSuccessful()) {
                    cb.onResult(ApiResult.exito(null, 200));
                } else {
                    manejarErroresEstandar(response.code(), response, cb);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (t.getMessage() != null && t.getMessage().contains("JsonReader")) {
                    cb.onResult(ApiResult.exito(null, 200));
                } else {
                    manejarFallaConexion(t, cb);
                }
            }
        });
    }

    public void actualizarProducto(ProductoDTO producto, Uri uri, Context context, ResultCallback<Void> cb) {
        try {
            String json = new com.google.gson.Gson().toJson(producto);
            RequestBody productoPart = RequestBody.create(json, MediaType.parse("application/json"));

            MultipartBody.Part imagenPart = null;
            if (uri != null) {
                imagenPart = prepararImagenPart(uri, context);
            }

            api.actualizarProducto(producto.getId(), productoPart, imagenPart).enqueue(new Callback<Void>() {
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
            cb.onResult(ApiResult.fallo(500, "Error al procesar imagen: " + e.getMessage()));
        }
    }

    // Manejo de errores
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


    private MultipartBody.Part prepararImagenPart(Uri uri, Context context) throws IOException {
        java.io.InputStream inputStream = context.getContentResolver().openInputStream(uri);
        byte[] bytes = readAllBytes(inputStream); // Método auxiliar para leer los bytes

        RequestBody requestFile = RequestBody.create(
                bytes,
                MediaType.parse(context.getContentResolver().getType(uri))
        );

        return MultipartBody.Part.createFormData("imagen", getFileName(uri, context), requestFile);
    }

    // Método para leer bytes del InputStream
    private byte[] readAllBytes(java.io.InputStream inputStream) throws IOException {
        java.io.ByteArrayOutputStream byteBuffer = new java.io.ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    // Método para obtener el nombre real del archivo desde el Uri
    private String getFileName(Uri uri, Context context) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (android.database.Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME);
                    if (index != -1) result = cursor.getString(index);
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) result = result.substring(cut + 1);
        }
        return result;
    }
}
