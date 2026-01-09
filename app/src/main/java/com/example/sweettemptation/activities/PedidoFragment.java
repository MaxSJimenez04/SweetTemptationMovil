package com.example.sweettemptation.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sweettemptation.R;
import com.example.sweettemptation.dto.ArchivoDTO;
import com.example.sweettemptation.dto.DetallesProductoDTO;
import com.example.sweettemptation.model.Pedido;
import com.example.sweettemptation.model.ProductoPedido;
import com.example.sweettemptation.servicios.ProductoPedidoService;
import com.example.sweettemptation.utils.Constantes;

import java.util.Collections;

public class PedidoFragment extends Fragment {

    private PedidoViewModel mViewModel;

    private ProgressBar progress;
    private TextView tvSubtotal, tvTotal, tvIva;
    private int idCliente = 3;
    private Pedido pedidoActual;
    private RecyclerView recycler;
    private DetallesProductoAdapter adapter;

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

        // Botones
        View btnCancelar = view.findViewById(R.id.btnCancelar);
        View btnPagar = view.findViewById(R.id.btnRealizarPedido);
        View btnEditar = view.findViewById(R.id.btnEditar);
        View btnGuardarCambios = view.findViewById(R.id.btnGuardarCambios);
        View btnProductos = view.findViewById(R.id.btnProductos);
        ImageView imgProducto = view.findViewById(R.id.imgProducto);
        View txtSinProducto = view.findViewById(R.id.txtSinProductos);

        // Inicializa ViewModel
        mViewModel = new ViewModelProvider(requireActivity()).get(PedidoViewModel.class);

        // Observers
        mViewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progress.setVisibility(Boolean.TRUE.equals(isLoading) ? View.VISIBLE : View.GONE);
        });

        mViewModel.getMensaje().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.isBlank()) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show();
                mViewModel.limpiarMensaje();
            }
        });


        mViewModel.cargarPedidoActual(idCliente);

//        SharedPreferences prefs = Objects.requireNonNull(getContext()).getSharedPreferences("user_prefs", MODE_PRIVATE);
//        idCliente = prefs.getInt("user_id", 3);

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
                }
        );
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        recycler.setAdapter(adapter);

        mViewModel.getPedidoActual().observe(getViewLifecycleOwner(), pedido -> {
            if (pedido == null) {
                adapter.submitList(Collections.emptyList());
                tvSubtotal.setText("Subtotal: $0");
                tvTotal.setText("Total: $0");
                txtSinProducto.setVisibility(View.VISIBLE);
                btnProductos.setVisibility(View.VISIBLE);
                recycler.setVisibility(View.GONE);
                return;
            }
            pedidoActual = pedido;

            mViewModel.consultarProductosPedido(pedidoActual.getId());
        });

        mViewModel.getSubtotalPedido().observe(getViewLifecycleOwner(), sub -> {
            tvSubtotal.setText("Subtotal: $" + sub);
        });

        mViewModel.getTotalPedido().observe(getViewLifecycleOwner(), tot -> {
            tvTotal.setText("Total: $" + tot);
        });

        tvIva.setText("IVA: " + Constantes.IVA + "%");

        btnEditar.setOnClickListener(v -> {
            btnGuardarCambios.setVisibility(View.VISIBLE);
            btnEditar.setVisibility(View.INVISIBLE);
            adapter.setModoEdicion(true);
        });
        btnGuardarCambios.setOnClickListener(v -> {
            adapter.setModoEdicion(false);
            mViewModel.recalcularTotal(pedidoActual.getId());
            btnGuardarCambios.setVisibility(View.GONE);
            btnEditar.setVisibility(View.VISIBLE);
        });
        btnProductos.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.fragmentProductosCliente);
        });

        // Eventos UI
        btnCancelar.setOnClickListener(v -> {
            mViewModel.cancelarPedido(pedidoActual.getId(), idCliente);
        });

        //TODO: borrar prueba generar ticket
        //Empieza grpc
        mViewModel.init(requireContext());
        mViewModel.ticketDescargado.observe(getViewLifecycleOwner(), uri -> {
            if (uri != null) {
                Toast.makeText(requireContext(),
                        "Ticket descargado correctamente",
                        Toast.LENGTH_LONG).show();
            }
        });
        btnPagar.setOnClickListener(v -> {
           mViewModel.descargarTicket(pedidoActual.getId());
        });


        btnPagar.setOnClickListener(v -> {
            //TODO:
           //NavHostFragment.findNavController(this).navigate(ID_FRAGMENT_PAGO);
        });

        mViewModel.getProductosPedido().observe(getViewLifecycleOwner(), lista -> {

            if (!lista.isEmpty()) {
                btnProductos.setVisibility(View.GONE);
                txtSinProducto.setVisibility(View.GONE);
                recycler.setVisibility(View.VISIBLE);
                adapter.submitList(lista);
                for (DetallesProductoDTO item : lista) {
                    mViewModel.cargarImagenProducto(item.getIdProducto(), new PedidoViewModel.ImagenCallback() {
                        @Override
                        public void onOk(int idProducto, ArchivoDTO archivo) {
                            Bitmap bmp = convertirArchivoABitmap(archivo);
                            if (bmp == null)
                                return;

                            new Handler(Looper.getMainLooper()).post(() -> {
                                adapter.setImagenProducto(idProducto, bmp);
                            });
                        }

                        @Override
                        public void onError(int idProducto, String mensaje) {
                            Bitmap phBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ph_imagenproducto);
                            new Handler(Looper.getMainLooper()).post(() ->{
                                adapter.setImagenProducto(idProducto, phBitmap);
                            });
                        }
                    });
                }
                mViewModel.calcularTotal();
            } else {
                btnProductos.setVisibility(View.VISIBLE);
                txtSinProducto.setVisibility(View.VISIBLE);
                recycler.setVisibility(View.GONE);
            }
        });
    }

    private Bitmap convertirArchivoABitmap(ArchivoDTO archivo) {
        if (archivo == null || archivo.getDatos() == null || archivo.getDatos().isEmpty()) {
            return null;
        }

        try {
            byte[] bytes = Base64.decode(archivo.getDatos(), Base64.DEFAULT);


            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (Exception e) {
            return null;
        }
    }
}
