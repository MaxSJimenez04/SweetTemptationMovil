package com.example.sweettemptation.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sweettemptation.R;
import com.example.sweettemptation.dto.ArchivoDTO;
import com.example.sweettemptation.dto.ProductoDTO;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ProductoFragment extends Fragment {

    private static final String TAG = "PRODUCTO_IMG";
    private ProductoViewModel mViewModel;
    private ProgressBar progress;
    private RecyclerView recycler;
    private ProductoAdapter adapter;
    private FloatingActionButton fabNuevo;
    private TextView txtSinResultados; // Nuevo: Para mostrar cuando no hay matches
    private SearchView svBuscador;     // Nuevo: El buscador

    public static ProductoFragment newInstance() {
        return new ProductoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_productos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicialización de vistas
        progress = view.findViewById(R.id.pbProgreso);
        recycler = view.findViewById(R.id.rvProductos);
        fabNuevo = view.findViewById(R.id.fabNuevoProducto);
        txtSinResultados = view.findViewById(R.id.txtSinResultados); // Asegúrate de añadirlo al XML
        svBuscador = view.findViewById(R.id.svBuscador);

        mViewModel = new ViewModelProvider(requireActivity()).get(ProductoViewModel.class);

        // Configuración del Adaptador
        adapter = new ProductoAdapter(
                producto -> abrirFormularioEditar(producto),
                producto -> confirmarEliminacion(producto),
                idProducto -> mViewModel.obtenerRutaArchivo(idProducto)
        );

        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        recycler.setAdapter(adapter);

        // --- CONFIGURACIÓN DEL BUSCADOR ---
        svBuscador.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filtrar(newText);
                validarListaVacia();
                return true;
            }
        });

        // --- OBSERVADORES ---

        mViewModel.getProductos().observe(getViewLifecycleOwner(), lista -> {
            if (lista != null) {
                // IMPORTANTE: setFullList para que el buscador conozca la lista nueva
                adapter.setFullList(lista);
                validarListaVacia();
            }
        });

        mViewModel.getImagenProducto().observe(getViewLifecycleOwner(), archivo -> {
            if (archivo != null && archivo.getDatos() != null) {
                Bitmap bmp = convertirArchivoABitmap(archivo);
                if (bmp != null) {
                    adapter.actualizarImagen(archivo.getIdProducto(), bmp);
                }
            }
        });

        mViewModel.getMensaje().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.isBlank()) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show();
                mViewModel.mensaje.setValue(null);
            }
        });

        mViewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (progress != null) {
                progress.setVisibility(Boolean.TRUE.equals(isLoading) ? View.VISIBLE : View.GONE);
            }
        });

        fabNuevo.setOnClickListener(v -> abrirFormularioRegistro());

        mViewModel.cargarProductos();
    }

    // Método para mostrar el texto "Sin resultados" si el filtro no encuentra nada
    private void validarListaVacia() {
        if (adapter.getItemCount() == 0) {
            txtSinResultados.setVisibility(View.VISIBLE);
        } else {
            txtSinResultados.setVisibility(View.GONE);
        }
    }

    private void confirmarEliminacion(ProductoDTO producto) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar Producto")
                .setMessage("¿Estás seguro de que deseas eliminar '" + producto.getNombre() + "'?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Sí, eliminar", (dialog, which) -> {
                    mViewModel.eliminarProducto(producto.getId());
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void abrirFormularioRegistro() {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new RegistrarProductoFragment())
                .addToBackStack(null)
                .commit();
    }

    private void abrirFormularioEditar(ProductoDTO producto) {
        DetalleProductoFragment detalleFrag = DetalleProductoFragment.newInstance(producto);
        getParentFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                        android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container, detalleFrag)
                .addToBackStack(null)
                .commit();
    }

    private Bitmap convertirArchivoABitmap(ArchivoDTO archivo) {
        if (archivo == null || archivo.getDatos() == null) return null;
        try {
            byte[] imageBytes = android.util.Base64.decode(archivo.getDatos(), android.util.Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        } catch (Exception e) {
            Log.e(TAG, "Error al decodificar: " + e.getMessage());
            return null;
        }
    }
}