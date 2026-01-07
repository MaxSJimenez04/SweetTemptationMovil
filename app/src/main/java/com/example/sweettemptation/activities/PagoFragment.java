package com.example.sweettemptation.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sweettemptation.R;
import com.example.sweettemptation.dto.PagoRequest;
import com.example.sweettemptation.interfaces.PagoApi;
import com.example.sweettemptation.servicios.PagoService;
import com.example.sweettemptation.utils.Constantes;
import com.google.android.material.button.MaterialButton;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PagoFragment extends AppCompatActivity {

    private RadioGroup rgMetodoPago;
    private LinearLayout lytEfectivo, lytTarjeta;
    private EditText etMontoRecibido;
    private TextView tvCambioCalculado, tvTotalLabel;
    private MaterialButton btnConfirmarPago;

    private PagoService pagoService;
    private int idPedido;
    private double totalAPagar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pago);

        idPedido = getIntent().getIntExtra("idPedido", 0);
        totalAPagar = getIntent().getDoubleExtra("totalPedido", 0.0);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constantes.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PagoApi api = retrofit.create(PagoApi.class);
        pagoService = new PagoService(api);

        rgMetodoPago = findViewById(R.id.rgMetodoPago);
        lytEfectivo = findViewById(R.id.lytEfectivo);
        lytTarjeta = findViewById(R.id.lytTarjeta);
        etMontoRecibido = findViewById(R.id.etMontoRecibido);
        tvCambioCalculado = findViewById(R.id.tvCambioCalculado);
        btnConfirmarPago = findViewById(R.id.btnConfirmarPago);
        tvTotalLabel = findViewById(R.id.tvTotal);

        if (tvTotalLabel != null) {
            tvTotalLabel.setText(String.format("Total a pagar: $%.2f", totalAPagar));
        }

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

        btnConfirmarPago.setOnClickListener(v -> enviarPago());
    }

    private void enviarPago() {
        int seleccionado = rgMetodoPago.getCheckedRadioButtonId();
        if (seleccionado == -1) {
            Toast.makeText(this, "Por favor seleccione un método de pago", Toast.LENGTH_SHORT).show();
            return;
        }

        String tipo = (seleccionado == R.id.rbEfectivo) ? "Efectivo" : "Tarjeta";
        double monto = 0;
        String detalles = "N/A";

        if (tipo.equals("Efectivo")) {
            String val = etMontoRecibido.getText().toString();
            if (val.isEmpty()) {
                Toast.makeText(this, "Ingrese el monto recibido", Toast.LENGTH_SHORT).show();
                return;
            }
            monto = Double.parseDouble(val);
            if (monto < totalAPagar) {
                Toast.makeText(this, "El monto es insuficiente", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            detalles = "Pago realizado con Tarjeta";
        }

        PagoRequest request = new PagoRequest(tipo, monto, detalles);

        pagoService.realizarPago(idPedido, request, result -> {
            if (result.isExito()) {
                Toast.makeText(this, result.datos.getMensajeConfirmacion(), Toast.LENGTH_LONG).show();
                finish(); 
            } else {
                Toast.makeText(this, result.mensaje, Toast.LENGTH_LONG).show();
            }
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
                    tvCambioCalculado.setTextColor(Color.parseColor("#4CAF50")); // Verde
                } else {
                    tvCambioCalculado.setText("Monto insuficiente");
                    tvCambioCalculado.setTextColor(Color.RED);
                }
            } catch (Exception e) {
                tvCambioCalculado.setText("Monto inválido");
            }
        } else {
            tvCambioCalculado.setText("Su cambio: $0.00");
        }
    }
}