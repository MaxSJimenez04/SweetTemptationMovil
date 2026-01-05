package com.example.sweettemptation.activities;

import static androidx.navigation.Navigation.findNavController;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.sweettemptation.R;

public class SeleccionarEstadisticasFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_seleccionar_estadisticas, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button btnEstadisticasVentas = view.findViewById(R.id.btnEstadisticasVentas);
        Button btnEstadisticasProductos = view.findViewById(R.id.btnEstadisticasProductos);
        View btnRegresar = view.findViewById(R.id.btnRegresar);

        btnEstadisticasVentas.setOnClickListener(v -> {
            //TODO: Navegar a estadisticasVentas
        });

        btnEstadisticasProductos.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_SeleccionarEstadisticas_EstadisticasProductos);
        });
        btnRegresar.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).popBackStack();
        });
    }
}
