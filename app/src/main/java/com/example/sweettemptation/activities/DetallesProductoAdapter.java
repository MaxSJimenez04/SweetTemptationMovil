package com.example.sweettemptation.activities;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.collection.SparseArrayCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sweettemptation.R;
import com.example.sweettemptation.dto.ArchivoDTO;
import com.example.sweettemptation.dto.DetallesProductoDTO;

import java.util.HashSet;
import java.util.Set;

public class DetallesProductoAdapter extends ListAdapter<DetallesProductoDTO, DetallesProductoAdapter.VH> {

    public interface OnEliminarClick {
        void onEliminar(DetallesProductoDTO item);
    }

    public interface OnCantidadCambiada {
        void onCantidadCambiada(DetallesProductoDTO item, int nuevaCantidad);
    }


    private final OnEliminarClick onEliminarClick;
    private final OnCantidadCambiada onCantidadCambiada;


    private boolean modoEdicion = false;

    private final SparseArrayCompat<Bitmap> imagenCache = new SparseArrayCompat<>();
    private final Set<Integer> imagenSolicitada = new HashSet<>();

    public DetallesProductoAdapter(OnEliminarClick onEliminarClick, OnCantidadCambiada onCantidadCambiada) {
        super(DIFF);
        this.onEliminarClick = onEliminarClick;
        this.onCantidadCambiada = onCantidadCambiada;
    }

    public void setModoEdicion(boolean enabled) {
        modoEdicion = enabled;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_detalle_producto, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        DetallesProductoDTO item = getItem(position);

        h.tvNombre.setText(item.getNombre());
        h.tvPrecio.setText("$" + item.getPrecio());
        h.tvCantidad.setText("Cantidad: " + item.getCantidad());
        h.tvNumero.setText(String.valueOf(item.getCantidad()));

        h.btnEliminar.setVisibility(modoEdicion ? View.VISIBLE : View.GONE);
        h.spSpinner.setVisibility(modoEdicion ? View.VISIBLE : View.GONE);
        h.tvCantidad.setVisibility(modoEdicion ? View.GONE : View.VISIBLE);

        Bitmap imagenProducto = imagenCache.get(item.getIdProducto());
        if (imagenProducto != null) {
            h.imgProducto.setImageBitmap(imagenProducto);
        } else {
            h.imgProducto.setImageResource(R.drawable.ph_imagenproducto);
        }
        h.btnEliminar.setOnClickListener(v -> {
            if (onEliminarClick != null) onEliminarClick.onEliminar(item);
        });

        h.btnRestar.setOnClickListener(v -> {
            int actual = parseIntSafe(h.tvNumero.getText().toString(), item.getCantidad());
            int nueva = Math.max(1, actual - 1);
            h.tvNumero.setText(String.valueOf(nueva));
            if (onCantidadCambiada != null) onCantidadCambiada.onCantidadCambiada(item, nueva);
        });

        h.btnAgregar.setOnClickListener(v -> {
            int actual = parseIntSafe(h.tvNumero.getText().toString(), item.getCantidad());
            int nueva = actual + 1;
            h.tvNumero.setText(String.valueOf(nueva));
            if (onCantidadCambiada != null) onCantidadCambiada.onCantidadCambiada(item, nueva);
        });
    }

    private int parseIntSafe(String s, int fallback) {
        try { return Integer.parseInt(s); } catch (Exception e) { return fallback; }
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView imgProducto;
        ImageButton btnEliminar;
        TextView tvNombre, tvPrecio, tvCantidad, tvNumero;
        LinearLayout spSpinner;
        Button btnRestar, btnAgregar;

        VH(@NonNull View itemView) {
            super(itemView);
            imgProducto = itemView.findViewById(R.id.imgProducto);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvPrecio = itemView.findViewById(R.id.tvPrecio);
            tvCantidad = itemView.findViewById(R.id.tvCantidad);
            spSpinner = itemView.findViewById(R.id.spSpinner);
            btnRestar = itemView.findViewById(R.id.btnRestar);
            tvNumero = itemView.findViewById(R.id.tvNumero);
            btnAgregar = itemView.findViewById(R.id.btnAgregar);
        }
    }

    private static final DiffUtil.ItemCallback<DetallesProductoDTO> DIFF =
            new DiffUtil.ItemCallback<DetallesProductoDTO>() {
                @Override
                public boolean areItemsTheSame(@NonNull DetallesProductoDTO oldItem, @NonNull DetallesProductoDTO newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull DetallesProductoDTO oldItem, @NonNull DetallesProductoDTO newItem) {
                    return oldItem.getCantidad() == newItem.getCantidad()
                            && safeEquals(oldItem.getNombre(), newItem.getNombre())
                            && safeEquals(oldItem.getPrecio(), newItem.getPrecio())
                            && safeEquals(oldItem.getIdProducto(), newItem.getIdProducto());
                }

                private boolean safeEquals(Object a, Object b) {
                    return (a == b) || (a != null && a.equals(b));
                }
            };

    public void setImagenProducto(int idProducto, Bitmap bitmap) {
        if (bitmap == null) return;

        imagenCache.put(idProducto, bitmap);
        for (int i = 0; i < getItemCount(); i++) {
            DetallesProductoDTO it = getItem(i);
            if (it != null && it.getIdProducto() == idProducto) {
                notifyItemChanged(i);
                break;
            }
        }
    }
}
