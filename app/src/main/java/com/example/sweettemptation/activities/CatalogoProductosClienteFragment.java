package com.example.sweettemptation.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sweettemptation.R;
import com.example.sweettemptation.dto.ProductoDTO;

import java.util.List;
import java.util.stream.Collectors;

public class CatalogoProductosClienteFragment extends Fragment {

    private ProductoViewModel mViewModel;
    private RecyclerView rvCatalogo;
    private CatalogoAdapter adapter;
    private ProgressBar progress;
    private SearchView svBuscador;

    public static CatalogoProductosClienteFragment newInstance() {
        return new CatalogoProductosClienteFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_catalogo_productos_cliente, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvCatalogo = view.findViewById(R.id.rvCatalogo);
        progress = view.findViewById(R.id.pbCargaCatalogo);
        svBuscador = view.findViewById(R.id.svBuscarCliente);

        mViewModel = new ViewModelProvider(requireActivity()).get(ProductoViewModel.class);
        setupRecyclerView();
        setupSearch();
        setupObservers();

        mViewModel.cargarProductos();
    }

    private void setupRecyclerView() {
        rvCatalogo.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new CatalogoAdapter(this::abrirModalDetalle);
        rvCatalogo.setAdapter(adapter);
    }

    private void setupObservers() {
        mViewModel.getProductos().observe(getViewLifecycleOwner(), lista -> {
            if (lista != null) {
                List<ProductoDTO> disponibles = lista.stream()
                        .filter(ProductoDTO::isDisponible)
                        .collect(Collectors.toList());
                adapter.setFullList(disponibles);

                for (ProductoDTO p : disponibles) {
                    mViewModel.obtenerRutaArchivo(p.getId());
                }
            }
        });

        mViewModel.getImagenProducto().observe(getViewLifecycleOwner(), archivo -> {
            if (archivo != null && archivo.getDatos() != null) {
                try {
                    byte[] decodedString = Base64.decode(archivo.getDatos(), Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    adapter.actualizarImagen(archivo.getIdProducto(), bitmap);
                } catch (Exception e) { e.printStackTrace(); }
            }
        });

        mViewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (progress != null) progress.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        // Mensajes de error del servidor
        mViewModel.getMensaje().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.isEmpty()) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
                mViewModel.mensaje.setValue(null);
            }
        });
    }

    private void setupSearch() {
        svBuscador.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filtrar(newText);
                return true;
            }
        });
    }

    private void abrirModalDetalle(ProductoDTO producto) {
        Bitmap imagenDeLaTarjeta = adapter.getImagenCargada(producto.getId());
        DetallePedidoBottomSheet modal = DetallePedidoBottomSheet.newInstance(producto, imagenDeLaTarjeta);
        modal.show(getChildFragmentManager(), "DetallePedido");
    }
}