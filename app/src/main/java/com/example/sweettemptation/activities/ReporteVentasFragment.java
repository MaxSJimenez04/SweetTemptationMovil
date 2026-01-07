package com.example.sweettemptation.activities;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sweettemptation.R;
import com.example.sweettemptation.interfaces.EstadisticasApi;
import com.example.sweettemptation.network.ApiCliente;
import com.example.sweettemptation.utils.Constantes; // Importación de tus constantes
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ReporteVentasFragment extends Fragment {

    private EstadisticasViewModel mViewModel;
    private ReporteVentasAdapter adapter;

    private TextInputEditText etFechaIni, etFechaFin;
    private Spinner spEstado;
    private ProgressBar progress;
    private ImageButton btnRegresar;
    private Button btnDescargar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reporte_ventas, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnRegresar = view.findViewById(R.id.btnRegresarReporte);
        etFechaIni = view.findViewById(R.id.etFechaInicial);
        etFechaFin = view.findViewById(R.id.etFechaFinal);
        spEstado = view.findViewById(R.id.spEstadoVenta);
        progress = view.findViewById(R.id.progressCircular);
        btnDescargar = view.findViewById(R.id.btnDescargarReporte);
        RecyclerView rvVentas = view.findViewById(R.id.rvVentas);

        adapter = new ReporteVentasAdapter();
        rvVentas.setLayoutManager(new LinearLayoutManager(getContext()));
        rvVentas.setAdapter(adapter);

        mViewModel = new ViewModelProvider(this).get(EstadisticasViewModel.class);
        configurarObservadores();

        String[] opcionesEstado = {"Todas", "Completada", "Cancelada"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, opcionesEstado);
        spEstado.setAdapter(spinnerAdapter);

        btnRegresar.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        etFechaIni.setOnClickListener(v -> abrirCalendario(etFechaIni));
        etFechaFin.setOnClickListener(v -> abrirCalendario(etFechaFin));
        view.findViewById(R.id.btnBuscarReporte).setOnClickListener(v -> ejecutarBusqueda());

        btnDescargar.setOnClickListener(v -> {
            String fIni = etFechaIni.getText().toString();
            String fFin = etFechaFin.getText().toString();
            String est = spEstado.getSelectedItem().toString();

            if (fIni.isEmpty() || fFin.isEmpty()) return;

            progress.setVisibility(View.VISIBLE);

            EstadisticasApi api = ApiCliente.getInstance().retrofit().create(EstadisticasApi.class);

            api.descargarReporte(fIni, fFin, est).enqueue(new retrofit2.Callback<okhttp3.ResponseBody>() {
                @Override
                public void onResponse(retrofit2.Call<okhttp3.ResponseBody> call, retrofit2.Response<okhttp3.ResponseBody> response) {
                    progress.setVisibility(View.GONE);
                    if (response.isSuccessful() && response.body() != null) {
                        // Ejecutamos el guardado en un hilo secundario para no trabar la app
                        new Thread(() -> guardarArchivoEnDescargas(response.body())).start();
                    } else {
                        Toast.makeText(getContext(), "Error de servidor: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<okhttp3.ResponseBody> call, Throwable t) {
                    progress.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Falla de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void configurarObservadores() {
        mViewModel.getVentas().observe(getViewLifecycleOwner(), lista -> {
            if (lista != null) {
                adapter.setVentas(lista);
                if (lista.isEmpty()) {
                    Toast.makeText(getContext(), "No se encontraron ventas", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mViewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (progress != null) progress.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        mViewModel.getMensaje().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) {
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                mViewModel.limpiarMensaje();
            }
        });
    }

    private void abrirCalendario(TextInputEditText campoTexto) {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Seleccione la fecha")
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            campoTexto.setText(sdf.format(new Date(selection)));
        });

        datePicker.show(getParentFragmentManager(), "DATE_PICKER");
    }

    private void ejecutarBusqueda() {
        String fIni = etFechaIni.getText().toString();
        String fFin = etFechaFin.getText().toString();
        String est = spEstado.getSelectedItem().toString();

        if (fIni.isEmpty() || fFin.isEmpty()) {
            Toast.makeText(getContext(), "Fechas incompleta(s)", Toast.LENGTH_SHORT).show();
            return;
        }
        mViewModel.consultarVentas(fIni, fFin, est);
    }

    private void prepararDescarga() {
        String fIni = etFechaIni.getText().toString();
        String fFin = etFechaFin.getText().toString();
        String est = spEstado.getSelectedItem().toString();

        if (fIni.isEmpty() || fFin.isEmpty()) {
            Toast.makeText(getContext(), "Realice una búsqueda primero", Toast.LENGTH_SHORT).show();
            return;
        }

        String urlFinal = Constantes.URL + "estadisticas/ventas/descargarCSV" +
                "?fechaInicio=" + fIni +
                "&fechaFin=" + fFin +
                "&estado=" + est;

        ejecutarDescargaManager(urlFinal);
    }

    private void ejecutarDescargaManager(String url) {
        try {
            SharedPreferences prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
            String token = prefs.getString("user_token", "");

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

            if (token != null && !token.isEmpty()) {
                request.addRequestHeader("Authorization", "Bearer " + token);
            }

            request.setTitle("Reporte Sweet Temptation");
            request.setDescription("Descargando reporte de ventas CSV...");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            String fileName = "Reporte_Ventas_" + System.currentTimeMillis() + ".csv";
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

            DownloadManager manager = (DownloadManager) requireContext().getSystemService(Context.DOWNLOAD_SERVICE);
            if (manager != null) {
                manager.enqueue(request);
                Toast.makeText(getContext(), "Descarga iniciada. Revisa tus notificaciones", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error al descargar: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void guardarArchivoEnDescargas(okhttp3.ResponseBody body) {
        try {
            String nombreArchivo = "Reporte_Ventas_" + System.currentTimeMillis() + ".csv";
            java.io.File file = new java.io.File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    nombreArchivo
            );

            java.io.InputStream inputStream = null;
            java.io.OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];
                inputStream = body.byteStream();
                outputStream = new java.io.FileOutputStream(file);

                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) break;
                    outputStream.write(fileReader, 0, read);
                }
                outputStream.flush();

                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Archivo guardado en Descargas", Toast.LENGTH_LONG).show());
            } finally {
                if (inputStream != null) inputStream.close();
                if (outputStream != null) outputStream.close();
            }
        } catch (java.io.IOException e) {
            requireActivity().runOnUiThread(() ->
                    Toast.makeText(getContext(), "Error al guardar archivo", Toast.LENGTH_SHORT).show());
        }
    }
}
