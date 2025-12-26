package com.example.sweettemptation.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sweettemptation.R;
import com.example.sweettemptation.dto.ArchivoDTO;
import com.example.sweettemptation.model.Pedido;
import com.example.sweettemptation.utils.Constantes;

import java.util.List;

public class pedido extends Fragment {

    private PedidoViewModel mViewModel;

    private ProgressBar progress;
    private TextView tvSubtotal, tvTotal, tvIva;
    private int idCliente;
    private Pedido pedidoActual;
    private RecyclerView recycler;
    private DetallesProductoAdapter adapter;

    public static pedido newInstance() {
        return new pedido();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pedido, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Views del layout
        progress = view.findViewById(R.id.pbProgreso);
        tvSubtotal = view.findViewById(R.id.txtSubtotal);
        tvTotal = view.findViewById(R.id.txtTotal);
        tvIva = view.findViewById(R.id.txtIva);
        recycler = view.findViewById(R.id.rvPedido);

        // Botones (ajusta ids)
        View btnCancelar = view.findViewById(R.id.btnCancelar);
        View btnPagar = view.findViewById(R.id.btnRealizarPedido);
        View btnEditar = view.findViewById(R.id.btnEditar);
        View btnGuardarCambios = view.findViewById(R.id.btnGuardarCambios);

        // Inicializa ViewModel
        mViewModel = new ViewModelProvider(requireActivity()).get(PedidoViewModel.class);

        // Observers
        mViewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progress.setVisibility(Boolean.TRUE.equals(isLoading) ? View.VISIBLE : View.GONE);
        });

        mViewModel.getMensaje().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.isBlank()) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show();
            }
        });

        mViewModel.cargarPedidoActual(idCliente);

        mViewModel.getPedidoActual().observe(getViewLifecycleOwner(), pedido -> {
            if (pedido != null){
                pedidoActual = pedido;
            }
        });

        mViewModel.recalcularTotal(pedidoActual.getId());

        mViewModel.consultarProductosPedido(pedidoActual.getId());

        mViewModel.getSubtotalPedido().observe(getViewLifecycleOwner(), sub -> {
            tvSubtotal.setText("Subtotal: $" + sub);
        });

        mViewModel.getTotalPedido().observe(getViewLifecycleOwner(), tot -> {
            tvTotal.setText("Total: $" + tot);
        });

        tvIva.setText("IVA: " + Constantes.IVA + "%");


            adapter = new DetallesProductoAdapter(
                item -> {
                    Pedido p = mViewModel.getPedidoActual().getValue();
                    if (p != null) mViewModel.eliminarProducto(p.getId(), item.getId());
                },
                (item, nuevaCantidad) -> {
                    Pedido p = mViewModel.getPedidoActual().getValue();
                    if (p != null) {
                        item.setCantidad(nuevaCantidad);
                        mViewModel.actualizarProducto(p.getId(), item);
                    }
                },
                idProducto -> {
                    mViewModel.cargarImagenProducto(idProducto, new PedidoViewModel.ImagenCallback() {
                        @Override
                        public void onOk(int idProd, ArchivoDTO archivo) {
                            Bitmap bmp = convertirArchivoABitmap(archivo);
                            if (bmp != null) adapter.setImagenProducto(idProd, bmp);
                        }
                        @Override
                        public void onError(int idProd, String mensaje) {
                            Toast.makeText(requireContext(), mensaje, Toast.LENGTH_LONG).show();
                        }
                    });
                }
        );

        recycler.setAdapter(adapter);

        mViewModel.getProductosPedido(/* idPedido */).observe(getViewLifecycleOwner(), lista -> {
            adapter.submitList(lista);
        });

        btnEditar.setOnClickListener(v ->{
            btnGuardarCambios.setVisibility(0);
            adapter.setModoEdicion(true);
        });
        btnGuardarCambios.setOnClickListener(v -> {
            adapter.setModoEdicion(false);
            btnGuardarCambios.setVisibility(8);
        });

        // Eventos UI
        btnCancelar.setOnClickListener(v -> mViewModel.cancelarPedido(pedidoActual.getId()));

        btnPagar.setOnClickListener(v -> {
            // Navegación la decide el Fragment/Activity (UI)
            // Ejemplo (Navigation Component):
            // NavHostFragment.findNavController(this).navigate(R.id.action_pedido_to_tipoPago);
        });

    }

    private Bitmap convertirArchivoABitmap(ArchivoDTO archivo) {
        if (archivo == null) return null;

        byte[] bytes = archivo.getDatos();
        if (bytes == null || bytes.length == 0) return null;

        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565; // menos memoria que ARGB_8888
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opt);
    }
}
