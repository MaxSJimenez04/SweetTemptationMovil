package com.example.sweettemptation.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

        // 1. Inicialización de vistas
        progress = view.findViewById(R.id.pbProgreso);
        recycler = view.findViewById(R.id.rvProductos);
        fabNuevo = view.findViewById(R.id.fabNuevoProducto);

        // 2. ViewModel compartido (importante usar requireActivity() si se navega entre fragments)
        mViewModel = new ViewModelProvider(requireActivity()).get(ProductoViewModel.class);

        // 3. Configuración del Adaptador con sus 3 Listeners
        adapter = new ProductoAdapter(
                producto -> abrirFormularioEditar(producto),   // Click Editar
                producto -> mViewModel.eliminarProducto(producto.getId()), // Click Eliminar
                idProducto -> {                                 // Carga de Imagen
                    Log.d(TAG, "Paso 1: El Adaptador pide imagen del ID: " + idProducto);
                    mViewModel.obtenerRutaArchivo(idProducto);
                }
        );

        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        recycler.setAdapter(adapter);

        // --- OBSERVADORES ---

        // Observar lista de productos
        mViewModel.getProductos().observe(getViewLifecycleOwner(), lista -> {
            if (lista != null) {
                adapter.submitList(lista);
            }
        });

        // Observar descarga de imágenes (EL PUNTO CRÍTICO)
        mViewModel.getImagenProducto().observe(getViewLifecycleOwner(), archivo -> {
            if (archivo != null && archivo.getDatos() != null) {
                Log.d(TAG, "Paso 2: Datos de imagen recibidos para producto: " + archivo.getIdProducto());

                Bitmap bmp = convertirArchivoABitmap(archivo);
                if (bmp != null) {
                    Log.d(TAG, "Paso 3: Actualizando adaptador con bitmap...");
                    // Se usa idProducto para sincronizar con la fila correcta
                    adapter.actualizarImagen(archivo.getIdProducto(), bmp);
                }
            }
        });

        // Observar mensajes
        mViewModel.getMensaje().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.isBlank()) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show();
                mViewModel.mensaje.setValue(null); // Limpiar mensaje después de mostrarlo
            }
        });

        // Observar ProgressBar
        mViewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (progress != null) {
                progress.setVisibility(Boolean.TRUE.equals(isLoading) ? View.VISIBLE : View.GONE);
            }
        });

        // 4. Botón Nuevo Producto
        fabNuevo.setOnClickListener(v -> abrirFormularioRegistro());

        // 5. Carga inicial
        mViewModel.cargarProductos();
    }

    private void abrirFormularioRegistro() {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new RegistrarProductoFragment())
                .addToBackStack(null)
                .commit();
    }

    private void abrirFormularioEditar(ProductoDTO producto) {
        Toast.makeText(getContext(), "Editar: " + producto.getNombre(), Toast.LENGTH_SHORT).show();
    }

    // Método optimizado para convertir bytes a imagen
    private Bitmap convertirArchivoABitmap(ArchivoDTO archivo) {
        if (archivo == null || archivo.getDatos() == null) {
            Log.e(TAG, "Error: Datos de archivo nulos.");
            return null;
        }

        try {
            // 1. Convertimos el String Base64 a un arreglo de bytes reales
            byte[] imageBytes = android.util.Base64.decode(archivo.getDatos(), android.util.Base64.DEFAULT);

            // 2. Creamos el Bitmap a partir de esos bytes
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        } catch (Exception e) {
            Log.e(TAG, "Error al decodificar la imagen: " + e.getMessage());
            return null;
        }
    }
}