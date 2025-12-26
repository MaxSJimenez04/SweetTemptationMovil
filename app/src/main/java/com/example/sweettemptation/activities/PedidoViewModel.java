package com.example.sweettemptation.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sweettemptation.dto.ArchivoDTO;
import com.example.sweettemptation.dto.DetallesArchivoDTO;
import com.example.sweettemptation.dto.DetallesProductoDTO;
import com.example.sweettemptation.dto.PedidoDTO;
import com.example.sweettemptation.dto.ProductoPedidoDTO;
import com.example.sweettemptation.interfaces.ApiResult;
import com.example.sweettemptation.interfaces.ArchivoApi;
import com.example.sweettemptation.interfaces.PedidoApi;
import com.example.sweettemptation.interfaces.ProductoPedidoApi;
import com.example.sweettemptation.model.Pedido;
import com.example.sweettemptation.network.ApiCliente;
import com.example.sweettemptation.servicios.ArchivoService;
import com.example.sweettemptation.servicios.PedidoService;
import com.example.sweettemptation.servicios.ProductoPedidoService;
import com.example.sweettemptation.utils.Constantes;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;

public class PedidoViewModel extends ViewModel {
    private final MutableLiveData<Pedido> pedidoActual = new MutableLiveData<>(null);
    private final MutableLiveData<List<DetallesProductoDTO>> productosPedido = new MutableLiveData<>(Collections.emptyList());
    public final MutableLiveData<Boolean> cargando = new MutableLiveData<>(false);
    public final MutableLiveData<String> mensaje = new MutableLiveData<>(null);
    public final MutableLiveData<ProductoPedidoDTO> productoActualizado = new MutableLiveData<>(null);
    public final MutableLiveData<DetallesArchivoDTO> detallesArchivo = new MutableLiveData<>(null);
    public final MutableLiveData<ArchivoDTO> archivoProducto = new MutableLiveData<>(null);
    public final MutableLiveData<BigDecimal> subtotalPedido = new MutableLiveData<>(BigDecimal.ZERO);
    public final MutableLiveData<BigDecimal> totalPedido = new MutableLiveData<>(BigDecimal.ZERO);

    private final PedidoService pedidoService;
    private final ProductoPedidoService productoPedidoService;
    private final ArchivoService archivoService;

    public PedidoViewModel(){
        PedidoApi pedidoApi = ApiCliente.getInstance().retrofit().create(PedidoApi.class);
        this.pedidoService = new PedidoService(pedidoApi);
        ProductoPedidoApi productoPedidoApi = ApiCliente.getInstance().retrofit().create(ProductoPedidoApi.class);
        this.productoPedidoService = new ProductoPedidoService(productoPedidoApi);
        ArchivoApi archivoApi = ApiCliente.getInstance().retrofit().create(ArchivoApi.class);
        this.archivoService = new ArchivoService(archivoApi);
    }

    public LiveData<Pedido> getPedidoActual(){
        return pedidoActual;
    }

    public LiveData<List<DetallesProductoDTO>> getProductosPedido(){
        return productosPedido;
    }

    public LiveData<Boolean> getLoading(){
        return cargando;
    }

    public LiveData<String> getMensaje(){
        return mensaje;
    }

    public LiveData<ProductoPedidoDTO> getProductoActualizado(){
        return productoActualizado;
    }

    public LiveData<DetallesArchivoDTO> getDetallesArchivo(){
        return detallesArchivo;
    }

    public LiveData<ArchivoDTO> getImagenProducto(){
        return archivoProducto;
    }

    public LiveData<BigDecimal> getSubtotalPedido(){
        return subtotalPedido;
    }

    public LiveData<BigDecimal> getTotalPedido(){
        return totalPedido;
    }

    public int getIVA(){
        return Constantes.IVA;
    }

    private BigDecimal calcularTotal(){
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal total = BigDecimal.ZERO;
        double subtotalOperacion = 0;
        List<DetallesProductoDTO> productosSubtotales = productosPedido.getValue();
        for (DetallesProductoDTO productoPedido :productosSubtotales) {
            double subtotalProducto = productoPedido.getSubtotal().doubleValue();
            subtotalOperacion += subtotalProducto;
        }
        subtotal = BigDecimal.valueOf(subtotalOperacion);
        subtotalPedido.postValue(subtotal);
        BigDecimal parseoIVA = BigDecimal.valueOf(Constantes.IVA/100);
        total = subtotal.multiply(parseoIVA);
        totalPedido.postValue(total);
        return total;
    }


    //MÉTODOS API
    public void cargarPedidoActual(int idCliente){
        cargando.setValue(true);
        mensaje.setValue(null);
        Call<PedidoDTO> callServicio = pedidoService.obtenerPedidoActual(idCliente, new PedidoService.ResultCallback<PedidoDTO>() {
            @Override
            public void onResult(ApiResult<PedidoDTO> result) {
                if (result.codigo == 200){
                    if (result.datos == null){
                        cargando.postValue(false);
                        mensaje.postValue("No se encontró pedido actual");
                        pedidoActual.postValue(null);
                    }else{
                        cargando.postValue(false);
                        PedidoDTO respuesta = result.datos;
                        Pedido respuestaApi = new Pedido(respuesta.getId(), respuesta.getFechaCompra(), respuesta.getActual(),
                                respuesta.getTotal(), respuesta.getEstado(),respuesta.getPersonalizado(),
                                respuesta.getIdCliente());
                        pedidoActual.postValue(respuestaApi);
                    }

                }else{
                    cargando.postValue(false);
                    mensaje.postValue(result.mensaje);
                }
            }
        });
    }

    public void crearPedido(int idCliente){
        cargando.setValue(true);
        mensaje.setValue(null);
        Call<Void> callServicio = pedidoService.crearPedido(idCliente, new PedidoService.ResultCallback<Void>() {
            @Override
            public void onResult(ApiResult<Void> result) {
                if (result.codigo == 201){
                    cargando.postValue(false);
                    mensaje.postValue(null);
                }else{
                    cargando.postValue(false);
                    mensaje.postValue(result.mensaje);
                }
            }
        });
    }

    public void cancelarPedido(int idPedido){
        cargando.setValue(true);
        mensaje.setValue(null);
        Call<Void> callServicio = pedidoService.cancelarPedido(idPedido, new PedidoService.ResultCallback<Void>() {
            @Override
            public void onResult(ApiResult<Void> result) {
               if (result.codigo == 200){
                   cargando.postValue(false);
                   //TODO: Crear Pedido con idCliente de app global
               }else{
                   cargando.postValue(false);
                   mensaje.postValue(result.mensaje);
               }
            }
        });
    }

    public void consultarProductosPedido(int idPedido){
        cargando.setValue(true);
        mensaje.setValue(null);
        Call<List<DetallesProductoDTO>> callServicio = productoPedidoService.consultarProductos(idPedido, new ProductoPedidoService.ResultCallBack<List<DetallesProductoDTO>>() {
            @Override
            public void onResult(ApiResult<List<DetallesProductoDTO>> result) {
                if(result.codigo == 200){
                    if(result.datos != null && !result.datos.isEmpty()){
                        cargando.postValue(false);
                        productosPedido.postValue(result.datos);
                    }else {
                        cargando.postValue(false);
                        productosPedido.postValue(null);
                    }
                }else {
                    cargando.postValue(false);
                    mensaje.postValue(result.mensaje);
                }
            }
        });
    }

    public void actualizarProducto(int idPedido, DetallesProductoDTO productoSeleccionado){
        cargando.setValue(true);
        if (productoSeleccionado == null){
            cargando.postValue(false);
            mensaje.postValue("Selecciona un producto válido");
        }

        ProductoPedidoDTO productoRespusta = new ProductoPedidoDTO(productoSeleccionado.getId(),
                productoSeleccionado.getSubtotal(), productoSeleccionado.getCantidad(),
                productoSeleccionado.getPrecio(), idPedido, productoSeleccionado.getIdProducto());

        Call<ProductoPedidoDTO> callServicio = productoPedidoService.actualizarProducto(idPedido, productoRespusta,
                new ProductoPedidoService.ResultCallBack<ProductoPedidoDTO>() {
            @Override
            public void onResult(ApiResult<ProductoPedidoDTO> result) {
                if (result.codigo == 200){
                    if (result.datos != null){
                        cargando.postValue(false);
                        productoActualizado.postValue(result.datos);
                        consultarProductosPedido(idPedido);
                    }else{
                        cargando.postValue(false);
                        mensaje.postValue(null);
                        productoActualizado.postValue(null);
                    }
                }else{
                    cargando.postValue(false);
                    mensaje.postValue(result.mensaje);
                }
            }
        });
    }

    public void recalcularTotal(int idPedido){
        mensaje.setValue(null);
        cargando.setValue(true);
        BigDecimal total = calcularTotal();
        Call<PedidoDTO> callServicio = productoPedidoService.recalcularTotal(idPedido, total, new ProductoPedidoService.ResultCallBack<PedidoDTO>() {
            @Override
            public void onResult(ApiResult<PedidoDTO> result) {
              if (result.codigo == 200){
                  cargando.postValue(false);
                  PedidoDTO respuesta = result.datos;
                  Pedido pedidoActualizado = new Pedido(respuesta.getId(),respuesta.getFechaCompra(),
                          respuesta.getActual(), respuesta.getTotal(), respuesta.getEstado(), respuesta.getPersonalizado(),
                          respuesta.getIdCliente());
                  pedidoActual.postValue(pedidoActualizado);
              }else{
                  cargando.postValue(false);
                  mensaje.postValue(result.mensaje);
              }
            }
        });
    }

    public void eliminarProducto(int idPedido, int idProducto){
        mensaje.setValue(null);
        cargando.setValue(true);
        Call<Void> callServicio = productoPedidoService.eliminarProducto(idPedido, idProducto, new ProductoPedidoService.ResultCallBack<Void>() {
            @Override
            public void onResult(ApiResult<Void> result) {
                if (result.codigo == 200){
                    cargando.postValue(false);
                    consultarProductosPedido(idPedido);
                    consultarProductosPedido(idPedido);
                }else {
                    cargando.postValue(false);
                    mensaje.postValue(result.mensaje);
                }
            }
        });
    }

    public void obtenerRutaArchivo(int idProducto){
        mensaje.setValue(null);
        cargando.setValue(true);
        Call<DetallesArchivoDTO> callServicio = archivoService.obtenerRutaArchivo(idProducto, new ArchivoService.ResultCallback<DetallesArchivoDTO>() {
            @Override
            public void onResult(ApiResult<DetallesArchivoDTO> result) {
                if (result.codigo == 200){
                    if (result.datos != null){
                        cargando.postValue(false);
                        detallesArchivo.postValue(result.datos);
                    }else {
                        cargando.postValue(false);
                        detallesArchivo.postValue(null);
                    }
                }else{
                    cargando.postValue(false);
                    mensaje.postValue(result.mensaje);
                }
            }
        });
    }

    public void obtenerArchivo(String ruta){
        cargando.setValue(true);
        mensaje.setValue(null);
        if (ruta == null){
            cargando.postValue(false);
            mensaje.postValue("Ruta vacía");
            return;
        }

        try{
           int idArchivo = extraerIdArchivo(ruta);
           Call<ArchivoDTO> callServicio = archivoService.obtenerImagen(idArchivo, new ArchivoService.ResultCallback<ArchivoDTO>() {
               @Override
               public void onResult(ApiResult<ArchivoDTO> result) {
                   if (result.codigo == 200){
                       if (result.datos != null){
                           cargando.postValue(false);
                           archivoProducto.postValue(result.datos);
                       } else {
                         cargando.postValue(false);
                         archivoProducto.postValue(null);
                       }
                   }else{
                       cargando.postValue(false);
                       mensaje.postValue(result.mensaje);
                   }
               }
           });
        }catch (IllegalArgumentException iae){
            cargando.postValue(false);
            mensaje.postValue(iae.getMessage());
        }

    }

    public static int extraerIdArchivo(String ruta) {
        if (ruta == null) throw new IllegalArgumentException("ruta null");

        String idArchivo = ruta;

        int idxSlash = idArchivo.lastIndexOf('/');
        if (idxSlash >= 0) idArchivo = idArchivo.substring(idxSlash + 1);

        int idxBackslash = idArchivo.lastIndexOf('\\');
        if (idxBackslash >= 0) idArchivo = idArchivo.substring(idxBackslash + 1);

        idArchivo = idArchivo.trim();

        if (idArchivo.isEmpty())
            throw new IllegalArgumentException("No se pudo extraer idArchivo de: " + ruta);

        try {
            return Integer.parseInt(idArchivo);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El idArchivo no es numérico: '" + idArchivo + "' (ruta: " + ruta + ")", e);
        }
    }

    public interface ImagenCallback {
        void onOk(int idProducto, ArchivoDTO archivo);
        void onError(int idProducto, String mensaje);
    }

    public void cargarImagenProducto(int idProducto, ImagenCallback cb) {
       obtenerRutaArchivo(idProducto);
       DetallesArchivoDTO detallesImagen = getDetallesArchivo().getValue();
       obtenerArchivo(detallesImagen.getRuta());
    }



}