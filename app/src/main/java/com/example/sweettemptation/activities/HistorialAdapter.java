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

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.ViewHolder> {

    private List<PedidoDTO> pedidos = new ArrayList<>();
    private final OnPedidoClickListener listener;

    public interface OnPedidoClickListener {
        void onPedidoClick(PedidoDTO pedido);
    }

    public HistorialAdapter(OnPedidoClickListener listener) {
        this.listener = listener;
    }

    public void setPedidos(List<PedidoDTO> nuevosPedidos) {
        this.pedidos = nuevosPedidos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_historial_pedido, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PedidoDTO pedido = pedidos.get(position);

        if (pedido.getEstado() == 3) {
            holder.txtEstado.setText("PAGADO");
            holder.txtEstado.setTextColor(Color.parseColor("#4CAF50")); // Verde
        } else {
            holder.txtEstado.setText("CANCELADO");
            holder.txtEstado.setTextColor(Color.RED);
        }

        if (pedido.getFechaCompra() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.getDefault());
            String fechaLimpia = pedido.getFechaCompra().format(formatter);
            holder.txtFecha.setText(fechaLimpia);
        }

        holder.txtTotal.setText(String.format("Total: $%.2f", pedido.getTotal()));

        holder.itemView.setOnClickListener(v -> listener.onPedidoClick(pedido));
    }

    @Override
    public int getItemCount() { return pedidos.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtEstado, txtFecha, txtTotal;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtEstado = itemView.findViewById(R.id.txtEstado);
            txtFecha = itemView.findViewById(R.id.txtFecha);
            txtTotal = itemView.findViewById(R.id.txtTotal);
        }
    }
}