package com.example.sweettemptation.activities;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sweettemptation.dto.PedidoDTO;
import com.example.sweettemptation.interfaces.EstadisticasApi;
import com.example.sweettemptation.network.ApiCliente;
import com.example.sweettemptation.servicios.EstadisticasService;

import java.util.List;

public class EstadisticasViewModel extends ViewModel {
    private final MutableLiveData<List<PedidoDTO>> ventas = new MutableLiveData<>();
    private final MutableLiveData<Boolean> cargando = new MutableLiveData<>(false);
    private final MutableLiveData<String> mensaje = new MutableLiveData<>();

    private final EstadisticasService service;

    public EstadisticasViewModel() {
        EstadisticasApi api = ApiCliente.getInstance().retrofit().create(EstadisticasApi.class);
        this.service = new EstadisticasService(api);
    }

    public LiveData<List<PedidoDTO>> getVentas() { return ventas; }
    public LiveData<Boolean> getLoading() { return cargando; }
    public LiveData<String> getMensaje() { return mensaje; }

    public void limpiarMensaje() {
        mensaje.setValue(null);
    }

    public void consultarVentas(String fechaInicio, String fechaFin, String estado) {
        cargando.setValue(true);
        mensaje.setValue(null);
        ventas.setValue(null);

        service.consultarVentas(fechaInicio, fechaFin, estado, result -> {
            cargando.postValue(false);
            if (result.isExito()) {
                ventas.postValue(result.datos);
            } else {
                mensaje.postValue(result.mensaje != null ? result.mensaje : "Error desconocido");
            }
        });
    }
}
