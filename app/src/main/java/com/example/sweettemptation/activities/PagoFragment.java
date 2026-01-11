package com.example.sweettemptation.activities;

import static android.widget.Toast.LENGTH_SHORT;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.example.sweettemptation.R;
import com.example.sweettemptation.auth.TokenStorage;
import com.example.sweettemptation.dto.DetallesProductoDTO;
import com.example.sweettemptation.dto.PagoRequest;
import com.example.sweettemptation.grpc.TicketRepository;
import com.example.sweettemptation.interfaces.PagoApi;
import com.example.sweettemptation.interfaces.ProductoPedidoApi;
import com.example.sweettemptation.network.ApiCliente;
import com.example.sweettemptation.servicios.PagoService;
import com.example.sweettemptation.servicios.ProductoPedidoService;
import com.example.sweettemptation.utils.Constantes;
import com.google.android.material.button.MaterialButton;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PagoFragment extends Fragment {

    private RadioGroup rgMetodoPago;
    private LinearLayout lytEfectivo, lytTarjeta;
    private EditText etMontoRecibido, etNombreTarjeta, etNumeroTarjeta, etFechaExpiracion, etCVV;
    private TextView tvCambioCalculado, tvTotalLabel;
    private MaterialButton btnConfirmarPago;
    private ProductoPedidoService productoPedidoService;
    private List<DetallesProductoDTO> productos;
    private PagoService pagoService;
    private int idPedido;
    private double totalAPagar;

    private android.net.Uri ticketDescargado;

    private final TicketRepository ticketRepository = new TicketRepository();

    public void init(Context context){
        ticketRepository.init(context);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pago, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(requireContext());

        if (getArguments() != null) {
            idPedido = getArguments().getInt("idPedido", 0);
            totalAPagar = getArguments().getDouble("totalPedido", 0.0);
        }

        PagoApi api = ApiCliente.getInstance().retrofit().create(PagoApi.class);
        ProductoPedidoApi productoPedidoApi = ApiCliente.getInstance().retrofit().create(ProductoPedidoApi.class);
        productoPedidoService = new ProductoPedidoService(productoPedidoApi);
        pagoService = new PagoService(api);

        rgMetodoPago = view.findViewById(R.id.rgMetodoPago);
        lytEfectivo = view.findViewById(R.id.lytEfectivo);
        lytTarjeta = view.findViewById(R.id.lytTarjeta);
        etMontoRecibido = view.findViewById(R.id.etMontoRecibido);
        etNombreTarjeta = view.findViewById(R.id.etNombreTarjeta);
        etNumeroTarjeta = view.findViewById(R.id.etNumeroTarjeta);
        etFechaExpiracion = view.findViewById(R.id.etFechaExpiracion);
        etCVV = view.findViewById(R.id.etCVV);
        tvCambioCalculado = view.findViewById(R.id.tvCambioCalculado);
        btnConfirmarPago = view.findViewById(R.id.btnConfirmarPago);
        tvTotalLabel = view.findViewById(R.id.tvTotal);

        if (tvTotalLabel != null) {
            tvTotalLabel.setText(String.format("Total a pagar: $%.2f", totalAPagar));
        }

        configurarLogicaUI();
        btnConfirmarPago.setOnClickListener(v -> validarYEnviar());
    }

    private void configurarLogicaUI() {
        rgMetodoPago.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbEfectivo) {
                lytEfectivo.setVisibility(View.VISIBLE);
                lytTarjeta.setVisibility(View.GONE);
            } else if (checkedId == R.id.rbTarjeta) {
                lytTarjeta.setVisibility(View.VISIBLE);
                lytEfectivo.setVisibility(View.GONE);
            }
        });

        etMontoRecibido.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                calcularCambio();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        etFechaExpiracion.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = s.toString();

                if (count == 1 && input.length() == 2 && !input.contains("/")) {
                    etFechaExpiracion.setText(input + "/");
                    etFechaExpiracion.setSelection(etFechaExpiracion.getText().length());
                }

                if (before == 1 && count == 0 && input.length() == 2 && input.endsWith("/")) {
                    etFechaExpiracion.setText(input.substring(0, 1));
                    etFechaExpiracion.setSelection(etFechaExpiracion.getText().length());
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void validarYEnviar() {
        int seleccionado = rgMetodoPago.getCheckedRadioButtonId();
        if (seleccionado == -1) {
            Toast.makeText(requireContext(), "Seleccione un método de pago", LENGTH_SHORT).show();
            return;
        }

        if (seleccionado == R.id.rbEfectivo) {
            String val = etMontoRecibido.getText().toString();
            if (val.isEmpty()) {
                etMontoRecibido.setError("Ingrese el monto");
                return;
            }
            double monto = Double.parseDouble(val);
            if (monto < totalAPagar) {
                etMontoRecibido.setError("Monto insuficiente");
                return;
            }
            ejecutarPeticion("Efectivo", monto, "N/A");

        } else if (seleccionado == R.id.rbTarjeta) {
            String nombre = etNombreTarjeta.getText().toString().trim();
            String numero = etNumeroTarjeta.getText().toString().trim();
            String fecha = etFechaExpiracion.getText().toString().trim();
            String cvv = etCVV.getText().toString().trim();

            if (nombre.isEmpty()) {
                etNombreTarjeta.setError("El nombre es requerido");
                return;
            }
            if (numero.length() != 16) {
                etNumeroTarjeta.setError("La tarjeta debe tener 16 dígitos");
                return;
            }
            if (fecha.length() != 5) {
                etFechaExpiracion.setError("Formato MM/AA requerido");
                return;
            }

            try {
                int mes = Integer.parseInt(fecha.substring(0, 2));
                int anio = Integer.parseInt(fecha.substring(3, 5));
                if (mes < 1 || mes > 12) {
                    etFechaExpiracion.setError("Mes inválido (01-12)");
                    return;
                }
                if (anio <= 26) {
                    etFechaExpiracion.setError("Año debe ser mayor a 26");
                    return;
                }
            } catch (Exception e) {
                etFechaExpiracion.setError("Fecha inválida");
                return;
            }

            if (cvv.length() < 3) {
                etCVV.setError("CVV inválido");
                return;
            }

            ejecutarPeticion("Tarjeta", totalAPagar, numero);
        }
    }

    private void ejecutarPeticion(String tipo, double monto, String detalles) {
        PagoRequest request = new PagoRequest(tipo, monto, detalles);
        productoPedidoService.consultarProductos(idPedido, result -> {
            if (result.codigo != 200 || result.datos == null || result.datos.isEmpty()){
                Toast.makeText(requireContext(), "No se encontraron productos", LENGTH_SHORT).show();
                return;
            }
            productos = result.datos;

            productoPedidoService.comprarProductos(idPedido, productos, respuesta -> {
                if (respuesta.codigo != 200){
                    Toast.makeText(requireContext(), "Uno o más productos agotaron sus existencias", LENGTH_SHORT).show();
                    return;
                }
                pagoService.realizarPago(idPedido, request, resultado -> {
                    if (result.isExito()) {
                        descargarTicket(idPedido);

                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), "Pago exitoso", Toast.LENGTH_LONG).show();
                            NavController navController =
                                    NavHostFragment.findNavController(
                                            requireActivity()
                                                    .getSupportFragmentManager()
                                                    .findFragmentById(R.id.nav_host)
                                    );
                            navController.navigate(
                                    R.id.fragmentProductosCliente,
                                    null,
                                    new NavOptions.Builder()
                                            .setPopUpTo(R.id.fragmentPedido, true)
                                            .build()
                            );
                        });
                    } else {
                        Toast.makeText(requireContext(), result.mensaje, Toast.LENGTH_LONG).show();
                    }
                });
            });
        });

    }

    private void calcularCambio() {
        String textoMonto = etMontoRecibido.getText().toString();
        if (!textoMonto.isEmpty()) {
            try {
                double montoRecibido = Double.parseDouble(textoMonto);
                double cambio = montoRecibido - totalAPagar;
                if (cambio >= 0) {
                    tvCambioCalculado.setText(String.format("Su cambio: $%.2f", cambio));
                    tvCambioCalculado.setTextColor(Color.parseColor("#4CAF50"));
                } else {
                    tvCambioCalculado.setText("Monto insuficiente");
                    tvCambioCalculado.setTextColor(Color.RED);
                }
            } catch (Exception e) {
                tvCambioCalculado.setText("Monto inválido");
            }
        }
    }


    public void descargarTicket(int idPedido) {
        ticketRepository.descargarTicket(idPedido, new TicketRepository.Callback() {
            @Override
            public void onSuccess(Uri uri) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    ticketDescargado = uri;
                    Toast.makeText(
                            requireContext(),
                            "Ticket descargado en la carpeta Descargas",
                            Toast.LENGTH_LONG
                    ).show();
                });
            }
            @Override
            public void onError(Throwable error) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(
                            requireContext(),
                            "Hubo un problema al descargar el ticket",
                            Toast.LENGTH_LONG
                    ).show();
                });
            }
        });
    }

}