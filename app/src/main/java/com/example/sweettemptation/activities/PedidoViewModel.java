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


    public LiveData<BigDecimal> getSubtotalPedido(){
        return subtotalPedido;
    }

    public LiveData<BigDecimal> getTotalPedido(){
        return totalPedido;
    }

    public int getIVA(){
        return Constantes.IVA;
    }

    public void calcularTotal(){
        BigDecimal subtotal;
        BigDecimal total;
        double subtotalOperacion = 0;
        List<DetallesProductoDTO> productosSubtotales = productosPedido.getValue();
        for (DetallesProductoDTO productoPedido :productosSubtotales) {
            double subtotalProducto = productoPedido.getSubtotal().doubleValue();
            subtotalOperacion += subtotalProducto;
        }
        subtotal = BigDecimal.valueOf(subtotalOperacion);
        subtotalPedido.postValue(subtotal);
        BigDecimal ivaRate = BigDecimal.valueOf(Constantes.IVA)
                .divide(BigDecimal.valueOf(100));

        BigDecimal iva = subtotal.multiply(ivaRate);
        total = subtotal.add(iva);
        totalPedido.postValue(total);
    }


    //MÉTODOS API
    public void cargarPedidoActual(int idCliente){
        cargando.setValue(true);
        mensaje.setValue(null);

        pedidoService.obtenerPedidoActual(idCliente, result -> {
            try {
                cargando.postValue(false);

                if (result == null) {
                    pedidoActual.postValue(null);
                    mensaje.postValue("ApiResult null");
                    return;
                }

                if (result.codigo == 200){
                    if (result.datos == null){
                        pedidoActual.postValue(null);
                        mensaje.postValue("No se encontró pedido actual");
                        crearPedido(idCliente);
                    } else {
                        PedidoDTO dto = result.datos;
                        Pedido p = new Pedido(dto.getId(), dto.getFechaCompra(), dto.getActual(),
                                dto.getTotal(), dto.getEstado(), dto.getPersonalizado(), dto.getIdCliente());
                        pedidoActual.postValue(p);
                    }
                } else {
                    mensaje.postValue(result.mensaje != null ? result.mensaje : ("Error " + result.codigo));
                }

            } catch (Exception e) {
                cargando.postValue(false);
                pedidoActual.postValue(null);
                mensaje.postValue("CRASH en callback: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            }
        });
    }

    public void crearPedido(int idCliente){
        cargando.setValue(true);
        mensaje.setValue(null);
        pedidoService.crearPedido(idCliente, result -> {
            if (result.codigo == 201){
                cargando.postValue(false);
                mensaje.postValue(null);
            }else{
                cargando.postValue(false);
                mensaje.postValue(result.mensaje);
            }
        });
    }

    public void cancelarPedido(int idPedido){
        cargando.setValue(true);
        mensaje.setValue(null);
        pedidoService.cancelarPedido(idPedido, result -> {
           if (result.codigo == 200){
               cargando.postValue(false);
               //TODO: Crear Pedido con idCliente de app global
           }else{
               cargando.postValue(false);
               mensaje.postValue(result.mensaje);
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
        BigDecimal total = getTotalPedido().getValue();
        productoPedidoService.recalcularTotal(idPedido, total, result -> {
            cargando.postValue(false);
            if (result.codigo == 200){
              PedidoDTO respuesta = result.datos;
              Pedido pedidoActualizado = new Pedido(respuesta.getId(),respuesta.getFechaCompra(),
                      respuesta.getActual(), respuesta.getTotal(), respuesta.getEstado(), respuesta.getPersonalizado(),
                      respuesta.getIdCliente());
              pedidoActual.postValue(pedidoActualizado);
          }else{
              mensaje.postValue(result.mensaje);
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
    public static int extraerIdArchivo(String ruta) {
        if (ruta == null) throw new IllegalArgumentException("ruta null");

        String s = ruta.trim();

        int q = s.indexOf('?');
        if (q >= 0) s = s.substring(0, q);

        int h = s.indexOf('#');
        if (h >= 0) s = s.substring(0, h);

        int slash = Math.max(s.lastIndexOf('/'), s.lastIndexOf('\\'));
        if (slash >= 0) s = s.substring(slash + 1);

        s = s.trim();

        int dot = s.indexOf('.');
        if (dot > 0) s = s.substring(0, dot);

        s = s.trim();
        if (s.isEmpty()) throw new IllegalArgumentException("No se pudo extraer idArchivo de: " + ruta);

        s = s.replaceAll("[^0-9]", "");
        if (s.isEmpty()) throw new IllegalArgumentException("El idArchivo no es numérico: " + ruta);

        return Integer.parseInt(s);
    }


    public interface ImagenCallback {
        void onOk(int idProducto, ArchivoDTO archivo);
        void onError(int idProducto, String mensaje);
    }

    public void cargarImagenProducto(int idProducto, ImagenCallback cb) {

        archivoService.obtenerRutaArchivo(idProducto, resRuta -> {
            if (resRuta.codigo != 200 || resRuta.datos == null || resRuta.datos.getRuta() == null) {
                cb.onError(idProducto, resRuta.mensaje != null ? resRuta.mensaje : "No se obtuvo ruta");
                return;
            }

            String ruta = resRuta.datos.getRuta();

            int idArchivo;
            try {
                idArchivo = extraerIdArchivo(ruta);
            } catch (IllegalArgumentException e) {
                cb.onError(idProducto, e.getMessage());
                return;
            }

            archivoService.obtenerImagen(idArchivo, resImg -> {
                if (resImg.codigo == 200 && resImg.datos != null && resImg.datos.getDatos() != null
                        && !resImg.datos.getDatos().isEmpty()) {
                    cb.onOk(idProducto, resImg.datos);
                } else {
                    cb.onError(idProducto, resImg.mensaje != null ? resImg.mensaje : "Imagen vacía");
                }
            });
        });
    }
}