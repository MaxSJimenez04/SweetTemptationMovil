package com.example.sweettemptation.activities;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sweettemptation.R;
import com.example.sweettemptation.dto.CategoriaDTO;
import com.example.sweettemptation.dto.ProductoDTO;

import java.math.BigDecimal;

public class RegistrarProductoFragment extends Fragment {

    private ProductoViewModel mViewModel;
    private EditText txtNombre, txtPrecio, txtStock, txtDesc;
    private Spinner cmbCategoria;
    private ImageView imgPreview;
    private ProgressBar progressCircular;
    private Button btnRegistrar;
    private Uri uriSeleccionada;

    // Para abrir la galería y obtener la imagen
    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    uriSeleccionada = uri;
                    imgPreview.setImageURI(uri);
                }
            });

    @Override
    public View onCreateView(@NonNull LayoutInflater inf, ViewGroup cont, Bundle sav) {
        return inf.inflate(R.layout.fragment_registrar_producto, cont, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtNombre = view.findViewById(R.id.txtNombreProducto);
        txtPrecio = view.findViewById(R.id.txtPrecioUnitario);
        txtStock = view.findViewById(R.id.txtUnidades);
        txtDesc = view.findViewById(R.id.txtDescripcion);
        cmbCategoria = view.findViewById(R.id.cmbCategoria);
        imgPreview = view.findViewById(R.id.imgProducto);
        progressCircular = view.findViewById(R.id.progressCircular);
        btnRegistrar = view.findViewById(R.id.btnRegistrar);

        mViewModel = new ViewModelProvider(requireActivity()).get(ProductoViewModel.class);

        configurarObservadores();

        mViewModel.cargarCategorias();

        view.findViewById(R.id.btnCargarImagen).setOnClickListener(v -> mGetContent.launch("image/*"));
        btnRegistrar.setOnClickListener(v -> registrar());
        view.findViewById(R.id.btnCancelar).setOnClickListener(v -> getParentFragmentManager().popBackStack());
    }

    private void configurarObservadores() {
        mViewModel.getCategorias().observe(getViewLifecycleOwner(), lista -> {
            if (lista != null) {
                ArrayAdapter<CategoriaDTO> adapter = new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_spinner_dropdown_item, lista);
                cmbCategoria.setAdapter(adapter);
            }
        });

        // Observar estado de carga (ProgressBar)
        mViewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                progressCircular.setVisibility(View.VISIBLE);
                btnRegistrar.setEnabled(false);
                btnRegistrar.setAlpha(0.5f);
            } else {
                progressCircular.setVisibility(View.GONE);
                btnRegistrar.setEnabled(true);
                btnRegistrar.setAlpha(1.0f);
            }
        });

        mViewModel.getMensaje().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) {
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                if (msg.contains("exitosamente")) {
                    getParentFragmentManager().popBackStack(); // Volver a la lista al tener éxito
                }
            }
        });
    }

    // TODO - revisar mas validaciones
    private void registrar() {
        String nombre = txtNombre.getText().toString().trim();
        String precioStr = txtPrecio.getText().toString().trim();
        String stockStr = txtStock.getText().toString().trim();
        String descripcion = txtDesc.getText().toString().trim();

        if (nombre.isEmpty()) {
            txtNombre.setError("Requerido");
            return;
        }
        if (precioStr.isEmpty()) {
            txtPrecio.setError("Requerido");
            return;
        }
        if (stockStr.isEmpty()) {
            txtStock.setError("Requerido");
            return;
        }
        if (uriSeleccionada == null) {
            Toast.makeText(getContext(), "Selecciona una imagen", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            BigDecimal precio = new BigDecimal(precioStr);
            int unidades = Integer.parseInt(stockStr);

            if (precio.compareTo(BigDecimal.ZERO) <= 0) {
                txtPrecio.setError("Debe ser mayor a 0");
                return;
            }

            ProductoDTO p = new ProductoDTO();
            p.setNombre(nombre);
            p.setPrecio(precio);
            p.setUnidades(unidades);
            p.setDescripcion(descripcion);
            p.setDisponible(true);

            CategoriaDTO cat = (CategoriaDTO) cmbCategoria.getSelectedItem();
            if (cat != null) {
                p.setCategoria(cat.getId());
            }

            mViewModel.guardarProductoConImagen(p, uriSeleccionada, requireContext());

        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Formato numérico inválido", Toast.LENGTH_SHORT).show();
        }
    }
}