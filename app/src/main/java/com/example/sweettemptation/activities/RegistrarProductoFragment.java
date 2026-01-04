package com.example.sweettemptation.activities;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
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
    private Uri uriSeleccionada;

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

        mViewModel = new ViewModelProvider(requireActivity()).get(ProductoViewModel.class);

        // Llenar Spinner
        mViewModel.getCategorias().observe(getViewLifecycleOwner(), lista -> {
            ArrayAdapter<CategoriaDTO> adapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_spinner_item, lista);
            cmbCategoria.setAdapter(adapter);
        });
        mViewModel.cargarCategorias();

        view.findViewById(R.id.btnCargarImagen).setOnClickListener(v -> mGetContent.launch("image/*"));
        view.findViewById(R.id.btnRegistrar).setOnClickListener(v -> registrar());
        view.findViewById(R.id.btnCancelar).setOnClickListener(v -> getParentFragmentManager().popBackStack());
    }

    private void registrar() {
        if (txtNombre.getText().toString().isEmpty() || uriSeleccionada == null) {
            Toast.makeText(getContext(), "Faltan datos o imagen", Toast.LENGTH_SHORT).show();
            return;
        }

        ProductoDTO p = new ProductoDTO();
        p.setNombre(txtNombre.getText().toString());
        p.setPrecio(new BigDecimal(txtPrecio.getText().toString()));
        p.setUnidades(Integer.parseInt(txtStock.getText().toString()));
        p.setDescripcion(txtDesc.getText().toString());

        CategoriaDTO cat = (CategoriaDTO) cmbCategoria.getSelectedItem();
        if (cat != null) p.setCategoria(cat.getId());

        mViewModel.guardarProductoConImagen(p, uriSeleccionada, requireContext());
    }
}
