package com.example.sweettemptation.activities;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sweettemptation.R;
import com.example.sweettemptation.dto.ProductoDTO;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ProductoAdapter extends ListAdapter<ProductoDTO, ProductoAdapter.ViewHolder> {

    private final OnProductoClickListener editListener;
    private final OnProductoClickListener deleteListener;
    private final OnImageLoadListener imageListener;
    private final Map<Integer, Bitmap> imagenesCargadas = new HashMap<>();

    public interface OnProductoClickListener {
        void onClick(ProductoDTO producto);
    }

    public interface OnImageLoadListener {
        void onLoad(int idProducto);
    }

    public ProductoAdapter(OnProductoClickListener edit, OnProductoClickListener delete, OnImageLoadListener imageLoad) {
        super(DIFF_CALLBACK);
        this.editListener = edit;
        this.deleteListener = delete;
        this.imageListener = imageLoad;
    }

    // Cambiamos el nombre para que coincida con el Fragmento
    public void actualizarImagen(int idProd, Bitmap bmp) {
        // 1. Guardamos la imagen en el mapa
        imagenesCargadas.put(idProd, bmp);

        // 2. Optimizamos: En lugar de refrescar TODA la lista,
        // buscamos la posición del producto para refrescar solo ese cuadrito.
        for (int i = 0; i < getCurrentList().size(); i++) {
            if (getCurrentList().get(i).getId() == idProd) {
                notifyItemChanged(i); // Esto hace que la imagen aparezca sin parpadeos
                break;
            }
        }
    }

    // Nombres de métodos corregidos: areItemsTheSame y areContentsTheSame
    private static final DiffUtil.ItemCallback<ProductoDTO> DIFF_CALLBACK = new DiffUtil.ItemCallback<ProductoDTO>() {
        @Override
        public boolean areItemsTheSame(@NonNull ProductoDTO oldItem, @NonNull ProductoDTO newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull ProductoDTO oldItem, @NonNull ProductoDTO newItem) {
            return oldItem.getNombre().equals(newItem.getNombre()) &&
                    oldItem.getPrecio().equals(newItem.getPrecio()) &&
                    oldItem.getUnidades() == newItem.getUnidades();
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductoDTO producto = getItem(position);
        Bitmap bmp = imagenesCargadas.get(producto.getId());
        holder.bind(producto, editListener, deleteListener, imageListener, bmp);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nombre, precio, stock;
        private final ImageButton btnEditar, btnEliminar;
        private final ImageView imagen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.txtItemNombre);
            precio = itemView.findViewById(R.id.txtItemPrecio);
            stock = itemView.findViewById(R.id.txtItemStock);
            btnEditar = itemView.findViewById(R.id.btnItemEditar);
            btnEliminar = itemView.findViewById(R.id.btnItemEliminar);
            imagen = itemView.findViewById(R.id.imgItemProducto);
        }

        public void bind(ProductoDTO p, OnProductoClickListener edit, OnProductoClickListener delete,
                         OnImageLoadListener imgLoad, Bitmap bitmap) {
            nombre.setText(p.getNombre());
            precio.setText(String.format(Locale.getDefault(), "$%.2f", p.getPrecio()));
            stock.setText("Stock: " + p.getUnidades());

            btnEditar.setOnClickListener(v -> edit.onClick(p));
            btnEliminar.setOnClickListener(v -> delete.onClick(p));

            if (bitmap != null) {
                imagen.setImageBitmap(bitmap);
            } else {
                imagen.setImageResource(R.drawable.ic_launcher_background);
                if (imgLoad != null) imgLoad.onLoad(p.getId());
            }
        }
    }
}