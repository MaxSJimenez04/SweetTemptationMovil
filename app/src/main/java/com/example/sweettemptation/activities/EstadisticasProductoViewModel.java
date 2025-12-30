package com.example.sweettemptation.activities;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sweettemptation.dto.EstadisticaProductoDTO;
import com.example.sweettemptation.dto.EstadisticaVentaProductoDTO;
import com.example.sweettemptation.dto.ProductoDTO;
import com.example.sweettemptation.interfaces.EstadisticasApi;
import com.example.sweettemptation.network.ApiCliente;
import com.example.sweettemptation.servicios.EstadisticasService;

import java.util.List;

public class EstadisticasProductoViewModel extends ViewModel {
    public final MutableLiveData<List<EstadisticaProductoDTO>> estadisticasProductosVendidos =
            new MutableLiveData<>(null);
    public final MutableLiveData<List<EstadisticaVentaProductoDTO>> ventasPorProducto =new MutableLiveData<>(null);

    public final MutableLiveData<List<ProductoDTO>> listaProductos = new MutableLiveData<>(null);

    private final EstadisticasService estadisticasService;
    //TODO: agregar servicio producto


    public EstadisticasProductoViewModel(){
        EstadisticasApi estadisticasApi = ApiCliente.getInstance().retrofit().create(EstadisticasApi.class);
        this.estadisticasService = new EstadisticasService(estadisticasApi);
    }

    public MutableLiveData<List<EstadisticaProductoDTO>> getEstadisticasProductosVendidos() {
        return estadisticasProductosVendidos;
    }

    public MutableLiveData<List<EstadisticaVentaProductoDTO>> getVentasPorProducto() {
        return ventasPorProducto;
    }

    public MutableLiveData<List<ProductoDTO>> getListaProductos() {
        return listaProductos;
    }

    public final MutableLiveData<Boolean> cargando = new MutableLiveData<>(false);
    public final MutableLiveData<String> mensaje = new MutableLiveData<>(null);
}
