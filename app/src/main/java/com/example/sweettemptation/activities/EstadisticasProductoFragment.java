package com.example.sweettemptation.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.sweettemptation.R;
import com.example.sweettemptation.dto.ProductoDTO;
import com.github.mikephil.charting.charts.LineChart;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class EstadisticasProductoFragment extends Fragment {
    private EstadisticasProductoViewModel mViewModel;

    private ProgressBar progreso;
    private TextView tvRangoSeleccionado;
    private List<ProductoDTO> listaProductos;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_estadisticasproducto, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progreso = view.findViewById(R.id.pbProgreso);
        tvRangoSeleccionado = view.findViewById(R.id.txtRangoFechas);
        Spinner cbRangoFechas = view.findViewById(R.id.cbRangoFechas);
        Spinner cbProducto = view.findViewById(R.id.cbProducto);
        View btnRegresar = view.findViewById(R.id.btnRegresar);
        LineChart lcGrafica = view.findViewById(R.id.lineChart);
        View tbMasVendidos = view.findViewById(R.id.tblVentasAltas);
        View tbMenosVendidos = view.findViewById(R.id.tblVentasBajas);
        mViewModel = new ViewModelProvider(requireActivity()).get(EstadisticasProductoViewModel.class);


        mViewModel.getCargando().observe(getViewLifecycleOwner(), isLoading -> {
            progreso.setVisibility(Boolean.TRUE.equals(isLoading) ? View.VISIBLE : View.GONE);
        });

        mViewModel.getMensaje().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.isBlank()) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show();
            }
        });

        ArrayAdapter<CharSequence> adapterRangos = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.rangos_fechas,
                android.R.layout.simple_spinner_item
        );
        adapterRangos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cbRangoFechas.setAdapter(adapterRangos);
        cbRangoFechas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean first = true;

            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                if (first) { first = false; }
                cbProducto.setEnabled(true);
                String seleccion = parent.getItemAtPosition(position).toString();
                tvRangoSeleccionado.setText(seleccion);

                EstadisticasProductoViewModel.RangoFechas rango = mViewModel.obtenerFechasRango(seleccion);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                tvRangoSeleccionado.setText("Rango seleccionado: " + rango.inicio.format(formatter) + " - "
                        + rango.fin.format(formatter));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                cbProducto.setEnabled(false);
            }
        });

        btnRegresar.setOnClickListener(v -> {
            NavHostFragment.findNavController(EstadisticasProductoFragment.this).popBackStack();
        });


    }
}
