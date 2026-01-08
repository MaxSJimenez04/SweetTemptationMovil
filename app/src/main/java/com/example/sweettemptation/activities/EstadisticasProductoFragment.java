package com.example.sweettemptation.activities;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.sweettemptation.R;
import com.example.sweettemptation.dto.EstadisticaProductoDTO;
import com.example.sweettemptation.dto.EstadisticaVentaProductoDTO;
import com.example.sweettemptation.dto.ProductoDTO;
import com.example.sweettemptation.utils.ProductosAdapter;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progreso = view.findViewById(R.id.pbProgreso);
        tvRangoSeleccionado = view.findViewById(R.id.txtRangoFechas);
        Spinner cbRangoFechas = view.findViewById(R.id.cbRangoFechas);
        Spinner cbProducto = view.findViewById(R.id.cbProducto);
        View btnRegresar = view.findViewById(R.id.btnRegresar);
        LineChart lcGrafica = view.findViewById(R.id.lineChart);
        TableLayout tbMasVendidos = view.findViewById(R.id.tblVentasAltas);
        TableLayout tbMenosVendidos = view.findViewById(R.id.tblVentasBajas);
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
        mViewModel.cargarProductos();
        mViewModel.getListaProductos().observe(getViewLifecycleOwner(), listaProductos ->{
            if (listaProductos == null || listaProductos.isEmpty()){
                return;
            }

            ProductosAdapter adapter = new ProductosAdapter(requireContext(), listaProductos);
            cbProducto.setAdapter(adapter);
        });
        mViewModel.getRango().observe(getViewLifecycleOwner(), rangoFechas -> {
            if (rangoFechas == null){
                return;
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            tvRangoSeleccionado.setText("Rango seleccionado: " + rangoFechas.inicio.format(formatter) + " - "
                    + rangoFechas.fin.format(formatter));
            mViewModel.cargarEstadisticasVentas(rangoFechas.inicio,rangoFechas.fin);

        });

        mViewModel.getVentasPorProducto().observe(getViewLifecycleOwner(), listaProductos ->{
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            if (listaProductos == null ||listaProductos.isEmpty()){
                return;
            }
            List<String> xValues = new ArrayList<>();

            for (EstadisticaVentaProductoDTO venta: listaProductos) {
                String fechaParseada = sdf.format(venta.getFecha());
                xValues.add(fechaParseada);
            }

            Description descripcion = new Description();
            descripcion.setText("Ventas por dia");
            descripcion.setPosition(150f, 15f);
            lcGrafica.getAxisRight().setDrawLabels(false);

            XAxis ejeX = lcGrafica.getXAxis();
            ejeX.setPosition(XAxis.XAxisPosition.BOTTOM);
            ejeX.setValueFormatter(new IndexAxisValueFormatter(xValues));
            ejeX.setGranularity(1f);
            ejeX.setLabelRotationAngle(-45f);
            ejeX.setDrawGridLines(false);
            ejeX.setLabelCount(Math.min(xValues.size(), 5), true);


            YAxis ejeY = lcGrafica.getAxisLeft();
            ejeY.setAxisMinimum(0f);
            ejeY.setDrawGridLines(false);
            ejeY.setLabelCount(6, true);

            lcGrafica.getAxisRight().setEnabled(false);

            List<Entry> entries = new ArrayList<>();

            for (int i = 0; i < listaProductos.size(); i++) {
                EstadisticaVentaProductoDTO dto = listaProductos.get(i);
                entries.add(new Entry(i, dto.getVentasPorDia()));
            }

            LineDataSet dataSet = new LineDataSet(entries, "Venta por dia");
            dataSet.setColor(R.color.primary);
            dataSet.setLineWidth(2f);
            dataSet.setCircleRadius(4f);
            dataSet.setValueTextSize(10f);
            dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

            LineData lineData = new LineData(dataSet);
            lcGrafica.setData(lineData);
            lcGrafica.animateX(800);
            lcGrafica.invalidate();
        });

        mViewModel.getEstadisticasProductosVendidos().observe(getViewLifecycleOwner(), listaProductos->{
            if (listaProductos == null || listaProductos.isEmpty()){
                return;
            }

            List<EstadisticaProductoDTO> masVendidos = new ArrayList<>();
            List<EstadisticaProductoDTO> menosVendidos = new ArrayList<>();

            for (EstadisticaProductoDTO estadistica: listaProductos) {
                switch (estadistica.getCategoria()){
                    case "MAS VENDIDOS":
                        masVendidos.add(estadistica);
                        break;
                    case "MENOS VENDIDOS":
                        menosVendidos.add(estadistica);
                        break;
                }
            }

            cargarTabla(tbMasVendidos, masVendidos);
            cargarTabla(tbMenosVendidos, menosVendidos);

        });

        cbRangoFechas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean first = true;

            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                if (first) { first = false; }
                cbProducto.setEnabled(true);
                String seleccion = parent.getItemAtPosition(position).toString();
                tvRangoSeleccionado.setText(seleccion);
                mViewModel.obtenerFechasRango(seleccion);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                cbProducto.setEnabled(false);
            }
        });



        cbProducto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ProductoDTO productoSeleccionado = (ProductoDTO) parent.getItemAtPosition(position);
                EstadisticasProductoViewModel.RangoFechas rango = mViewModel.getRango().getValue();

                mViewModel.consultarVentasPorProducto(productoSeleccionado.getId(), rango.inicio, rango.fin);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mViewModel.mensaje.postValue("Sin producto seleccionado");
            }
        });

        btnRegresar.setOnClickListener(v -> {
            NavHostFragment.findNavController(EstadisticasProductoFragment.this).popBackStack();
        });
    }


    private void cargarTabla(TableLayout tableLayout,
                             List<EstadisticaProductoDTO> lista) {

        // Mantener solo el header (fila 0)
        if (tableLayout.getChildCount() > 1) {
            tableLayout.removeViews(1, tableLayout.getChildCount() - 1);
        }

        int maxFilas = 5; // las que tienes en el XML
        int filas = Math.min(lista.size(), maxFilas);

        for (int i = 0; i < filas; i++) {
            EstadisticaProductoDTO dto = lista.get(i);

            TableRow row = new TableRow(requireContext());

            // Columna producto
            TextView tvProducto = new TextView(requireContext());
            tvProducto.setLayoutParams(new TableRow.LayoutParams(
                    0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            tvProducto.setText(dto.getNombre());
            tvProducto.setPadding(8, 8, 8, 8);

            // Columna ventas
            TextView tvVentas = new TextView(requireContext());
            tvVentas.setLayoutParams(new TableRow.LayoutParams(
                    0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            tvVentas.setText(String.valueOf(dto.getVentas()));
            tvVentas.setPadding(8, 8, 8, 8);
            tvVentas.setGravity(Gravity.END);

            row.addView(tvProducto);
            row.addView(tvVentas);

            tableLayout.addView(row);
        }
    }

}
