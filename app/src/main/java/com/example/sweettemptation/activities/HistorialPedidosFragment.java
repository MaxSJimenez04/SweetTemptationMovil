package com.example.sweettemptation.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sweettemptation.R;
import com.example.sweettemptation.utils.UserSession;

public class HistorialPedidosFragment extends Fragment {

    private HistorialViewModel hViewModel;
    private HistorialAdapter adapter;
    private TextView txtPaginaNum;
    private Button btnAnterior, btnSiguiente;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_historial_pedido, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rvHistorial = view.findViewById(R.id.rvHistorial);
        txtPaginaNum = view.findViewById(R.id.txtPaginaNum);
        btnAnterior = view.findViewById(R.id.btnAnterior);
        btnSiguiente = view.findViewById(R.id.btnSiguiente);

        adapter = new HistorialAdapter(pedido -> {
            Bundle bundle = new Bundle();
            bundle.putInt("idPedido", pedido.getId());
            NavHostFragment.findNavController(this).navigate(R.id.fragmentResumenPedido, bundle);
        });

        rvHistorial.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvHistorial.setAdapter(adapter);

        hViewModel = new ViewModelProvider(this).get(HistorialViewModel.class);

        hViewModel.pedidosVisibles.observe(getViewLifecycleOwner(), lista -> {
            if (lista != null) {
                adapter.setPedidos(lista);
                actualizarBotonesPaginacion();
            }
        });

        hViewModel.paginaActual.observe(getViewLifecycleOwner(), pagina -> {
            txtPaginaNum.setText("Página " + pagina);
        });

        int idCliente = UserSession.getUserId(requireContext());
        hViewModel.cargarHistorial(idCliente);

        btnAnterior.setOnClickListener(v -> hViewModel.mostrarPagina(hViewModel.paginaActual.getValue() - 1));
        btnSiguiente.setOnClickListener(v -> hViewModel.mostrarPagina(hViewModel.paginaActual.getValue() + 1));
    }

    private void actualizarBotonesPaginacion() {
        btnAnterior.setEnabled(hViewModel.tieneAnterior());
        btnSiguiente.setEnabled(hViewModel.tieneSiguiente());
    }
}