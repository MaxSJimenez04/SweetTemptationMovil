package com.example.sweettemptation.activities;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sweettemptation.R;
import com.example.sweettemptation.dto.PedidoDTO;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReporteVentasAdapter extends RecyclerView.Adapter<ReporteVentasAdapter.ViewHolder> {

    private List<PedidoDTO> listaVentas = new ArrayList<>();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "MX"));

    public void setVentas(List<PedidoDTO> ventas) {
        this.listaVentas = (ventas != null) ? ventas : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_venta_reporte, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PedidoDTO venta = listaVentas.get(position);

        // 1. Mostrar Usuario/Rol (Sincronizado con mapearRol de tu API)
        String tipoUsuario;
        switch (venta.getIdRol()) {
            case 1: tipoUsuario = "Administrador"; break;
            case 2: tipoUsuario = "Empleado"; break;
            case 3: tipoUsuario = "Cliente"; break;
            default: tipoUsuario = "Desconocido"; break;
        }
        holder.txtUsuario.setText("Usuario: " + tipoUsuario);
        if (venta.getTotal() != null) {
            holder.txtTotal.setText(currencyFormat.format(venta.getTotal()));
        }
        if (venta.getFechaCompra() != null) {
            holder.txtFecha.setText("Fecha: " + venta.getFechaCompra().format(formatter));
        } else {
            holder.txtFecha.setText("Fecha: No disponible");
        }
        holder.txtTipo.setText(Boolean.TRUE.equals(venta.getPersonalizado()) ? "Pedido: Personalizado" : "Pedido: Estándar");
        configurarEstado(holder.txtEstado, venta.getEstado());
    }

    private void configurarEstado(TextView tv, int codigoEstado) {
        if (codigoEstado == 3) {
            tv.setText("COMPLETADA");
            tv.setTextColor(Color.parseColor("#2E7D32")); // Verde oscuro
            tv.setBackgroundColor(Color.parseColor("#E8F5E9")); // Fondo verde claro
        } else if (codigoEstado == 4) {
            tv.setText("CANCELADA");
            tv.setTextColor(Color.parseColor("#C62828")); // Rojo
            tv.setBackgroundColor(Color.parseColor("#FFEBEE")); // Fondo rojo claro
        } else if (codigoEstado == 2) {
            tv.setText("PENDIENTE");
            tv.setTextColor(Color.parseColor("#F57C00")); // Naranja
            tv.setBackgroundColor(Color.parseColor("#FFF3E0")); // Fondo naranja claro
        } else {
            tv.setText("DESCONOCIDO");
            tv.setTextColor(Color.GRAY);
            tv.setBackgroundColor(Color.parseColor("#F5F5F5"));
        }
    }

    @Override
    public int getItemCount() {
        return listaVentas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtUsuario, txtTotal, txtFecha, txtTipo, txtEstado;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUsuario = itemView.findViewById(R.id.txtVentaUsuario);
            txtTotal = itemView.findViewById(R.id.txtVentaTotal);
            txtFecha = itemView.findViewById(R.id.txtVentaFecha);
            txtTipo = itemView.findViewById(R.id.txtVentaTipo);
            txtEstado = itemView.findViewById(R.id.txtVentaEstado);
        }
    }
}
