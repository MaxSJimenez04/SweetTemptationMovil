package com.example.sweettemptation.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.sweettemptation.R;
import com.example.sweettemptation.auth.TokenStorage;
import com.example.sweettemptation.dto.SolicitudPersonalizadaDTO;
import com.example.sweettemptation.interfaces.PedidoPersonalizadoApi;
import com.example.sweettemptation.servicios.PedidoCustomService;
import com.example.sweettemptation.utils.Constantes;
import com.example.sweettemptation.utils.UserSession;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SolicitarPedidoCustomFragment extends Fragment {

    private ImageView imgPastelPreview;
    private Spinner spSabor, spRelleno, spTamano, spCobertura;
    private EditText etEspecificaciones;
    private Button btnEnviar;
    private ProgressBar pbProgreso;
    private Uri imageUri;
    private PedidoCustomService service;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_solicitar_pedido_custom, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imgPastelPreview = view.findViewById(R.id.imgPastelPreview);
        spSabor = view.findViewById(R.id.spSabor);
        spRelleno = view.findViewById(R.id.spRelleno);
        spTamano = view.findViewById(R.id.spTamano);
        spCobertura = view.findViewById(R.id.spCobertura);
        etEspecificaciones = view.findViewById(R.id.etEspecificaciones);
        btnEnviar = view.findViewById(R.id.btnEnviarSolicitud);
        pbProgreso = view.findViewById(R.id.pbProgreso);

        configurarSpinners();
        inicializarServicio();

        ActivityResultLauncher<Intent> launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        imgPastelPreview.setImageURI(imageUri);
                        imgPastelPreview.setAlpha(1.0f);
                    }
                }
        );

        view.findViewById(R.id.btnSubirImagen).setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            launcher.launch(i);
        });

        btnEnviar.setOnClickListener(v -> validarYEnviar());
    }

    private void inicializarServicio() {
        try {
            TokenStorage storage = new TokenStorage(requireContext());
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(chain -> {
                Request r = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer " + storage.getToken())
                        .build();
                return chain.proceed(r);
            }).build();

            Retrofit r = new Retrofit.Builder()
                    .baseUrl(Constantes.URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            service = new PedidoCustomService(r.create(PedidoPersonalizadoApi.class));
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error al inicializar servicios", Toast.LENGTH_SHORT).show();
        }
    }

    private void configurarSpinners() {
        setupSpinner(spSabor, new String[]{"Vainilla", "Chocolate", "Red Velvet", "Fresa"});
        setupSpinner(spRelleno, new String[]{"Crema Pastelera", "Chocolate", "Frutos Rojos", "Cajeta"});
        setupSpinner(spTamano, new String[]{"Mini", "Pequeño", "Mediano", "Grande"});
        setupSpinner(spCobertura, new String[]{"Fondant", "Merengue", "Betún Queso", "Ganache"});
    }

    private void setupSpinner(Spinner spinner, String[] opciones) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, opciones);
        spinner.setAdapter(adapter);
    }

    private void validarYEnviar() {
        String specs = etEspecificaciones.getText().toString().trim();
        if (specs.isEmpty()) {
            etEspecificaciones.setError("Por favor describe tu pedido");
            return;
        }

        int idCliente = UserSession.getUserId(requireContext());
        String tel = requireContext().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
                .getString("user_phone", "No proporcionado");

        SolicitudPersonalizadaDTO req = new SolicitudPersonalizadaDTO(
                idCliente,
                spTamano.getSelectedItem().toString(),
                spSabor.getSelectedItem().toString(),
                spRelleno.getSelectedItem().toString(),
                spCobertura.getSelectedItem().toString(),
                specs,
                imageUri != null ? imageUri.toString() : "sin-imagen",
                tel
        );

        btnEnviar.setEnabled(false);
        if (pbProgreso != null) pbProgreso.setVisibility(View.VISIBLE);

        service.enviarSolicitud(req, result -> {
            if (isAdded()) {
                btnEnviar.setEnabled(true);
                if (pbProgreso != null) pbProgreso.setVisibility(View.GONE);

                if (result.isExito()) {
                    Toast.makeText(requireContext(), "¡Solicitud enviada correctamente!", Toast.LENGTH_LONG).show();
                    NavHostFragment.findNavController(this).popBackStack();
                } else {
                    String msgError = result.mensaje != null ? result.mensaje : Constantes.MENSAJE_FALLA_SERVIDOR;
                    if (result.codigo == 503) msgError = Constantes.MENSAJE_SIN_CONEXION;

                    Toast.makeText(requireContext(), "Error: " + msgError, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}