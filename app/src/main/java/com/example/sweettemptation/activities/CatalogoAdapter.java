package com.example.sweettemptation.activities;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sweettemptation.R;
import com.example.sweettemptation.dto.ProductoDTO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CatalogoAdapter extends ListAdapter<ProductoDTO, CatalogoAdapter.ViewHolder> {

    private final OnCatalogoClickListener listener;
    private List<ProductoDTO> listaCompleta = new ArrayList<>();
    private final Map<Integer, Bitmap> imagenesCargadas = new HashMap<>();

    public interface OnCatalogoClickListener {
        void onProductoClick(ProductoDTO producto);
    }

    public CatalogoAdapter(OnCatalogoClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    public void setFullList(List<ProductoDTO> list) {
        this.listaCompleta = new ArrayList<>(list);
        submitList(list);
    }

    public void filtrar(String texto) {
        if (texto == null || texto.isEmpty()) {
            submitList(new ArrayList<>(listaCompleta));
        } else {
            String query = texto.toLowerCase().trim();
            List<ProductoDTO> filtrados = new ArrayList<>();
            for (ProductoDTO p : listaCompleta) {
                if (p.getNombre().toLowerCase().contains(query)) {
                    filtrados.add(p);
                }
            }
            submitList(filtrados);
        }
    }

    public void actualizarImagen(int idProd, Bitmap bmp) {
        imagenesCargadas.put(idProd, bmp);
        List<ProductoDTO> listaActual = getCurrentList();
        for (int i = 0; i < listaActual.size(); i++) {
            if (listaActual.get(i).getId() == idProd) {
                notifyItemChanged(i);
                break;
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto_catalogo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductoDTO producto = getItem(position);
        Bitmap bmp = imagenesCargadas.get(producto.getId());
        holder.bind(producto, listener, bmp);
    }

    private static final DiffUtil.ItemCallback<ProductoDTO> DIFF_CALLBACK = new DiffUtil.ItemCallback<ProductoDTO>() {
        @Override
        public boolean areItemsTheSame(@NonNull ProductoDTO oldItem, @NonNull ProductoDTO newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull ProductoDTO oldItem, @NonNull ProductoDTO newItem) {
            return oldItem.getNombre().equals(newItem.getNombre()) &&
                    oldItem.getPrecio().equals(newItem.getPrecio());
        }
    };

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nombre, precio, descripcion;
        private final ImageView imagen;
        private final Button btnVerDetalle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.txtCatNombre);
            precio = itemView.findViewById(R.id.txtCatPrecio);
            descripcion = itemView.findViewById(R.id.txtCatDesc);
            imagen = itemView.findViewById(R.id.imgCatProducto);
            btnVerDetalle = itemView.findViewById(R.id.btnCatVerDetalle);
        }

        public void bind(ProductoDTO p, OnCatalogoClickListener listener, Bitmap bitmap) {
            nombre.setText(p.getNombre());
            precio.setText(String.format(Locale.getDefault(), "$%.2f", p.getPrecio()));
            descripcion.setText(p.getDescripcion());

            if (bitmap != null) {
                imagen.setImageBitmap(bitmap);
            } else {
                imagen.setImageResource(R.drawable.ic_launcher_background);
            }

            View.OnClickListener clickAction = v -> listener.onProductoClick(p);
            btnVerDetalle.setOnClickListener(clickAction);
            itemView.setOnClickListener(clickAction);
        }
    }
    public Bitmap getImagenCargada(int idProducto) {
        return imagenesCargadas.get(idProducto);
    }
}