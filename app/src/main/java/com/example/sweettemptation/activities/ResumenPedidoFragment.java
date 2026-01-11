package com.example.sweettemptation.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sweettemptation.R;

public class ResumenPedidoFragment extends Fragment {

    private PedidoViewModel mViewModel;
    private DetallesProductoAdapter adapter;
    private int idPedido;
    private ProgressBar progress;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pedido, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            idPedido = getArguments().getInt("idPedido");
        }

        progress = view.findViewById(R.id.pbProgreso);
        TextView txtTitulo = view.findViewById(R.id.txtTitulo);
        txtTitulo.setText("Resumen del Pedido");

        view.findViewById(R.id.btnRealizarPedido).setVisibility(View.GONE);
        view.findViewById(R.id.btnCancelar).setVisibility(View.GONE);
        view.findViewById(R.id.btnEditar).setVisibility(View.GONE);
        view.findViewById(R.id.btnGuardarCambios).setVisibility(View.GONE);

        view.findViewById(R.id.btnRegresar).setOnClickListener(v ->
                NavHostFragment.findNavController(this).popBackStack());

        RecyclerView recycler = view.findViewById(R.id.rvPedido);
        adapter = new DetallesProductoAdapter(item -> {}, (item, qty) -> {});
        adapter.setModoEdicion(false);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        recycler.setAdapter(adapter);

        mViewModel = new ViewModelProvider(requireActivity()).get(PedidoViewModel.class);

        mViewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progress.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        mViewModel.getProductosPedido().observe(getViewLifecycleOwner(), lista -> {
            if (lista != null) {
                adapter.submitList(lista);
                mViewModel.calcularTotal();
            }
        });

        mViewModel.getTotalPedido().observe(getViewLifecycleOwner(), total -> {
            ((TextView)view.findViewById(R.id.txtTotal)).setText("Total: $" + total);
        });

        mViewModel.getSubtotalPedido().observe(getViewLifecycleOwner(), sub -> {
            ((TextView)view.findViewById(R.id.txtSubtotal)).setText("Subtotal: $" + sub);
        });

        mViewModel.consultarProductosPedido(idPedido);
    }
}