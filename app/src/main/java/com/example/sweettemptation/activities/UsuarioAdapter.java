package com.example.sweettemptation.activities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sweettemptation.R;
import com.example.sweettemptation.dto.UsuarioResponse;

import java.util.ArrayList;
import java.util.List;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder> {

    private List<UsuarioResponse> usuarios = new ArrayList<>();
    private OnUsuarioClickListener listener;

    public interface OnUsuarioClickListener {
        void onEditarClick(UsuarioResponse usuario);
        void onEliminarClick(UsuarioResponse usuario);
    }

    public UsuarioAdapter(OnUsuarioClickListener listener) {
        this.listener = listener;
    }

    public void setUsuarios(List<UsuarioResponse> usuarios) {
        this.usuarios = usuarios;
        notifyDataSetChanged();
    }

    public List<UsuarioResponse> getUsuarios() {
        return usuarios;
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_usuario, parent, false);
        return new UsuarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {
        UsuarioResponse usuario = usuarios.get(position);
        holder.bind(usuario);
    }

    @Override
    public int getItemCount() {
        return usuarios.size();
    }

    class UsuarioViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivIcono;
        private TextView tvNombre;
        private TextView tvCorreo;
        private TextView tvRol;
        private ImageButton btnEditar;
        private ImageButton btnEliminar;

        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcono = itemView.findViewById(R.id.ivIcono);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvCorreo = itemView.findViewById(R.id.tvCorreo);
            tvRol = itemView.findViewById(R.id.tvRol);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }

        public void bind(UsuarioResponse usuario) {
            tvNombre.setText(usuario.getNombreCompleto());
            tvCorreo.setText(usuario.getCorreo());
            tvRol.setText(usuario.getNombreRol());

            int iconTint;
            switch (usuario.getIdRol()) {
                case 1:
                    iconTint = itemView.getContext().getColor(R.color.error);
                    break;
                case 2:
                    iconTint = itemView.getContext().getColor(R.color.primary);
                    break;
                default:
                    iconTint = itemView.getContext().getColor(R.color.text_secondary);
                    break;
            }
            ivIcono.setColorFilter(iconTint);

            btnEditar.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditarClick(usuario);
                }
            });

            btnEliminar.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEliminarClick(usuario);
                }
            });
        }
    }
}
