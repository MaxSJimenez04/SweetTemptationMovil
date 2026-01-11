package com.example.sweettemptation.activities;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.sweettemptation.dto.PedidoDTO;
import com.example.sweettemptation.interfaces.PedidoApi;
import com.example.sweettemptation.network.ApiCliente;
import com.example.sweettemptation.servicios.PedidoService;
import java.util.ArrayList;
import java.util.List;

public class HistorialViewModel extends ViewModel {
    private final PedidoService service;
    private final MutableLiveData<List<PedidoDTO>> _pedidosVisibles = new MutableLiveData<>();
    public final LiveData<List<PedidoDTO>> pedidosVisibles = _pedidosVisibles;

    private List<PedidoDTO> todosLosPedidos = new ArrayList<>();
    public final MutableLiveData<Integer> paginaActual = new MutableLiveData<>(1);
    public final MutableLiveData<Boolean> cargando = new MutableLiveData<>(false);
    private final int TAMANO_PAGINA = 20;

    public HistorialViewModel() {
        PedidoApi api = ApiCliente.getInstance().retrofit().create(PedidoApi.class);
        this.service = new PedidoService(api);
    }

    public void cargarHistorial(int idCliente) {
        cargando.setValue(true);
        service.obtenerHistorial(idCliente, result -> {
            cargando.postValue(false);
            if (result.codigo == 200 && result.datos != null) {
                // Ordenar: Más recientes primero
                todosLosPedidos = result.datos;
                todosLosPedidos.sort((p1, p2) -> p2.getFechaCompra().compareTo(p1.getFechaCompra()));
                mostrarPagina(1);
            } else if (result.codigo == 204) {
                _pedidosVisibles.postValue(new ArrayList<>());
            }
        });
    }

    public void mostrarPagina(int pagina) {
        int inicio = (pagina - 1) * TAMANO_PAGINA;
        int fin = Math.min(inicio + TAMANO_PAGINA, todosLosPedidos.size());

        if (inicio < todosLosPedidos.size() && inicio >= 0) {
            _pedidosVisibles.postValue(new ArrayList<>(todosLosPedidos.subList(inicio, fin)));
            paginaActual.postValue(pagina);
        }
    }

    public boolean tieneSiguiente() {
        return (paginaActual.getValue() * TAMANO_PAGINA) < todosLosPedidos.size();
    }

    public boolean tieneAnterior() {
        return paginaActual.getValue() > 1;
    }
}