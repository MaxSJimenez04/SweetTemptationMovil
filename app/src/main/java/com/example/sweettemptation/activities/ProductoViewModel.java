package com.example.sweettemptation.activities;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sweettemptation.dto.ArchivoDTO;
import com.example.sweettemptation.dto.CategoriaDTO;
import com.example.sweettemptation.dto.ProductoDTO;
import com.example.sweettemptation.interfaces.ArchivoApi;
import com.example.sweettemptation.interfaces.CategoriaApi;
import com.example.sweettemptation.interfaces.ProductoApi;
import com.example.sweettemptation.network.ApiCliente;
import com.example.sweettemptation.servicios.ArchivoService;
import com.example.sweettemptation.servicios.CategoriaService;
import com.example.sweettemptation.servicios.ProductoService;

import java.util.Collections;
import java.util.List;

public class ProductoViewModel extends ViewModel {

    private final MutableLiveData<List<ProductoDTO>> productos = new MutableLiveData<>(Collections.emptyList());
    private final MutableLiveData<List<CategoriaDTO>> categorias = new MutableLiveData<>(Collections.emptyList());
    public final MutableLiveData<Boolean> cargando = new MutableLiveData<>(false);
    public final MutableLiveData<String> mensaje = new MutableLiveData<>(null);
    private final MutableLiveData<ArchivoDTO> archivoProducto = new MutableLiveData<>(null);

    private final ProductoService productoService;
    private final ArchivoService archivoService;
    private final CategoriaService categoriaService;

    public ProductoViewModel() {
        // Inicialización de APIs
        ProductoApi pApi = ApiCliente.getInstance().retrofit().create(ProductoApi.class);
        ArchivoApi aApi = ApiCliente.getInstance().retrofit().create(ArchivoApi.class);
        CategoriaApi cApi = ApiCliente.getInstance().retrofit().create(CategoriaApi.class);

        // Inicialización de Servicios
        this.productoService = new ProductoService(pApi);
        this.archivoService = new ArchivoService(aApi);
        this.categoriaService = new CategoriaService(cApi);
    }

    // --- GETTERS PARA LA UI ---
    public LiveData<List<ProductoDTO>> getProductos() { return productos; }
    public LiveData<List<CategoriaDTO>> getCategorias() { return categorias; }
    public LiveData<Boolean> getLoading() { return cargando; }
    public LiveData<String> getMensaje() { return mensaje; }
    public LiveData<ArchivoDTO> getImagenProducto() { return archivoProducto; }

    // --- MÉTODOS DE PRODUCTOS ---

    public void cargarProductos() {
        cargando.setValue(true);
        productoService.listarProductos(result -> {
            cargando.postValue(false);
            if (result.codigo == 200) {
                productos.postValue(result.datos);
            } else {
                mensaje.postValue(result.mensaje);
            }
        });
    }

    public void cargarCategorias() {
        categoriaService.listarCategorias(result -> {
            if (result.codigo == 200) {
                categorias.postValue(result.datos);
            }
        });
    }

    /**
     * MÉTODO CORREGIDO: Ahora recibe Uri y Context para soportar Multipart.
     * De esta forma no necesitas modificar tu API de Spring Boot.
     */
    public void guardarProductoConImagen(ProductoDTO producto, Uri uri, Context context) {
        cargando.setValue(true);
        mensaje.setValue(null);

        // Llamamos al servicio pasando los 3 parámetros necesarios para Multipart
        productoService.crearProducto(producto, uri, context, result -> {
            cargando.postValue(false);
            if (result.codigo == 200 || result.codigo == 201) {
                mensaje.postValue("¡Producto registrado exitosamente!");
                cargarProductos(); // Refrescamos la lista automáticamente
            } else {
                mensaje.postValue(result.mensaje);
            }
        });
    }

    public void eliminarProducto(int idProducto) {
        cargando.setValue(true);
        productoService.eliminarProducto(idProducto, result -> {
            cargando.postValue(false);
            if (result.codigo == 200 || result.codigo == 204) {
                mensaje.postValue("Producto eliminado correctamente");
                cargarProductos(); // Actualiza la lista al instante
            } else {
                mensaje.postValue(result.mensaje);
            }
        });
    }

    // --- LÓGICA DE CARGA DE IMÁGENES (PARA LA LISTA) ---

    public void obtenerRutaArchivo(int idProducto) {
        archivoService.obtenerDetallesArchivo(idProducto, result -> {
            if (result.codigo == 200 && result.datos != null) {
                int idArchivoReal = result.datos.getId();
                descargarImagenReal(idArchivoReal, idProducto);
            }
        });
    }

    private void descargarImagenReal(int idArchivo, int idProducto) {
        archivoService.obtenerImagen(idArchivo, result -> {
            if (result.codigo == 200 && result.datos != null) {
                // Obtenemos el String Base64 (JPG en texto)
                String datosBase64 = result.datos.getDatos();

                if (datosBase64 != null) {
                    android.util.Log.d("IMAGEN_TEST", "Imagen cargada para producto: " + idProducto);
                    result.datos.setIdProducto(idProducto);
                    archivoProducto.postValue(result.datos);
                }
            }
        });
    }
}