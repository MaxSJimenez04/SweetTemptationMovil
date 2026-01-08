package com.example.sweettemptation.activities;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sweettemptation.dto.EstadisticaProductoDTO;
import com.example.sweettemptation.dto.EstadisticaVentaProductoDTO;
import com.example.sweettemptation.dto.ProductoDTO;
import com.example.sweettemptation.interfaces.ApiResult;
import com.example.sweettemptation.interfaces.EstadisticasApi;
import com.example.sweettemptation.interfaces.ProductoApi;
import com.example.sweettemptation.network.ApiCliente;
import com.example.sweettemptation.servicios.EstadisticasService;
import com.example.sweettemptation.servicios.ProductoService;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

import retrofit2.Call;

public class EstadisticasProductoViewModel extends ViewModel {
    public final MutableLiveData<List<EstadisticaProductoDTO>> estadisticasProductosVendidos =
            new MutableLiveData<>(null);
    public final MutableLiveData<List<EstadisticaVentaProductoDTO>> ventasPorProducto =new MutableLiveData<>(null);
    public final MutableLiveData<Boolean> cargando = new MutableLiveData<>(false);
    public final MutableLiveData<String> mensaje = new MutableLiveData<>(null);
    public final MutableLiveData<RangoFechas> rango = new MutableLiveData<>(null);
    public final MutableLiveData<List<ProductoDTO>> listaProductos = new MutableLiveData<>(null);

    private final EstadisticasService estadisticasService;
    private final ProductoService productoService;


    public EstadisticasProductoViewModel(){
        EstadisticasApi estadisticasApi = ApiCliente.getInstance().retrofit().create(EstadisticasApi.class);
        this.estadisticasService = new EstadisticasService(estadisticasApi);
        ProductoApi productoApi = ApiCliente.getInstance().retrofit().create(ProductoApi.class);
        this.productoService = new ProductoService(productoApi);
    }

    public LiveData<Boolean> getCargando() {
        return cargando;
    }

    public LiveData<String> getMensaje() {
        return mensaje;
    }
    public LiveData<RangoFechas> getRango(){return rango;}

    public LiveData<List<EstadisticaProductoDTO>> getEstadisticasProductosVendidos() {
        return estadisticasProductosVendidos;
    }

    public LiveData<List<EstadisticaVentaProductoDTO>> getVentasPorProducto() {
        return ventasPorProducto;
    }

    public LiveData<List<ProductoDTO>> getListaProductos() {
        return listaProductos;
    }

    public static class RangoFechas {
        public final LocalDate inicio;
        public final LocalDate fin; // inclusive

        public RangoFechas(LocalDate inicio, LocalDate fin) {
            this.inicio = inicio;
            this.fin = fin;
        }
    }
    public void obtenerFechasRango(String rangoSeleccionado){
        LocalDate hoy = LocalDate.now();
        switch (rangoSeleccionado){
            case "Semana pasada":
                  rango.postValue(new RangoFechas(hoy.minusDays(6), hoy));
                  break;
            case "Quincena pasada":
                 rango.postValue(new RangoFechas(hoy.minusDays(14), hoy));
                 break;
            case "Mes pasado":
                YearMonth mes = YearMonth.from(hoy).minusMonths(1);
                LocalDate inicio = mes.atDay(1);
                LocalDate fin = mes.atEndOfMonth();
                rango.postValue(new RangoFechas(inicio,fin));
                break;
            default:
                 rango.postValue(new RangoFechas(hoy.minusDays(6), hoy));
        }
    }

    //Metodos API
    public void cargarEstadisticasVentas(LocalDate fechaInicio, LocalDate fechaFin){
        cargando.setValue(true);
        mensaje.setValue(null);
        Call<List<EstadisticaProductoDTO>> callServicio = estadisticasService.obtenerEstadisticasProductos(
                fechaInicio, fechaFin, new EstadisticasService.ResultCallback<List<EstadisticaProductoDTO>>() {
                    @Override
                    public void onResult(ApiResult<List<EstadisticaProductoDTO>> result) {
                        if (result.codigo == 200){
                            if (result.datos != null){
                                cargando.postValue(false);
                                estadisticasProductosVendidos.postValue(result.datos);
                            }else{
                                cargando.postValue(false);
                                mensaje.postValue("No se encontraron datos");
                            }
                        }else {
                            cargando.postValue(false);
                            mensaje.postValue(result.mensaje);
                        }
                    }
                }
        );
    }

    public void consultarVentasPorProducto(int idProducto, LocalDate fechaInicio, LocalDate fechaFin){
        cargando.setValue(true);
        mensaje.setValue(null);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        String fechaInicioParseada = fechaInicio.format(formatter);
        String fechaFinParseada = fechaFin.format(formatter);
        Call<List<EstadisticaVentaProductoDTO>> callServicio = estadisticasService.obtenerVentaProducto(idProducto,
                fechaInicioParseada, fechaFinParseada, new EstadisticasService.ResultCallback<List<EstadisticaVentaProductoDTO>>() {
                    @Override
                    public void onResult(ApiResult<List<EstadisticaVentaProductoDTO>> result) {
                       if (result.codigo == 200){
                           if (result.datos != null){
                               cargando.postValue(false);
                               ventasPorProducto.postValue(result.datos);
                           }else {
                               cargando.postValue(false);
                               mensaje.postValue("No se encontraron datos para este producto");
                           }
                       }else {
                           cargando.postValue(false);
                           mensaje.postValue(result.mensaje);
                       }
                    }
                });
    }

    public void cargarProductos() {
        cargando.setValue(true);
        productoService.listarProductos(result -> {
            cargando.postValue(false);
            if (result.codigo == 200) {
                listaProductos.postValue(result.datos);
            } else {
                mensaje.postValue(result.mensaje);
            }
        });
    }
}
