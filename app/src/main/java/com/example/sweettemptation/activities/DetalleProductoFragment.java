package com.example.sweettemptation.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sweettemptation.R;
import com.example.sweettemptation.dto.CategoriaDTO;
import com.example.sweettemptation.dto.ProductoDTO;
import com.google.android.material.textfield.TextInputEditText;

import java.math.BigDecimal;
import java.util.ArrayList;

public class DetalleProductoFragment extends Fragment {

    private ProductoDTO producto;
    private ProductoViewModel mViewModel;
    private boolean modoEdicion = false;
    private Uri nuevaImagenUri;

    // Vistas UI
    private TextInputEditText etNombre, etPrecio, etStock, etDesc;
    private Spinner cmbCategoria;
    private Switch swDisponible;
    private ImageView imgProducto;
    private Button btnAccion, btnCancelar, btnCambiarFoto;
    private ImageButton btnRegresar;

    // Para abrir la galería de fotos
    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    nuevaImagenUri = uri;
                    imgProducto.setImageURI(uri);
                }
            });

    public static DetalleProductoFragment newInstance(ProductoDTO p) {
        DetalleProductoFragment fragment = new DetalleProductoFragment();
        Bundle args = new Bundle();
        args.putSerializable("producto", p);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            producto = (ProductoDTO) getArguments().getSerializable("producto");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detalle_producto, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity()).get(ProductoViewModel.class);

        initViews(view);
        configurarObservadores();

        // Cargar datos
        mViewModel.cargarCategorias();
        llenarDatosIniciales();
        mViewModel.obtenerRutaArchivo(producto.getId());

        // Configurar botones
        btnAccion.setOnClickListener(v -> {
            if (!modoEdicion) {
                activarModoEdicion(true);
            } else {
                ejecutarActualizacion();
            }
        });

        btnCancelar.setOnClickListener(v -> {
            activarModoEdicion(false);
            limpiarErrores();
            llenarDatosIniciales();
            nuevaImagenUri = null;
        });

        btnCambiarFoto.setOnClickListener(v -> mGetContent.launch("image/*"));

        btnRegresar.setOnClickListener(v -> regresarAlListado());

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                regresarAlListado();
            }
        });
    }

    private void initViews(View v) {
        etNombre = v.findViewById(R.id.etDetalleNombre);
        etPrecio = v.findViewById(R.id.etDetallePrecio);
        etStock = v.findViewById(R.id.etDetalleStock);
        etDesc = v.findViewById(R.id.etDetalleDesc);
        cmbCategoria = v.findViewById(R.id.cmbCategoriaDetalle);
        swDisponible = v.findViewById(R.id.swDetalleDisponible);
        imgProducto = v.findViewById(R.id.imgDetalleProducto);
        btnAccion = v.findViewById(R.id.btnModificarAccion);
        btnCancelar = v.findViewById(R.id.btnCancelarModif);
        btnCambiarFoto = v.findViewById(R.id.btnCambiarFoto);
        btnRegresar = v.findViewById(R.id.btnRegresarDetalle);
    }

    private void llenarDatosIniciales() {
        if (producto != null) {
            etNombre.setText(producto.getNombre());
            etPrecio.setText(producto.getPrecio().toString());
            etStock.setText(String.valueOf(producto.getUnidades()));
            etDesc.setText(producto.getDescripcion());
            swDisponible.setChecked(producto.isDisponible());
        }
    }

    private void configurarObservadores() {
        mViewModel.getCategorias().observe(getViewLifecycleOwner(), lista -> {
            if (lista != null) {
                ArrayAdapter<CategoriaDTO> adapter = new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_spinner_dropdown_item, lista);
                cmbCategoria.setAdapter(adapter);

                for (int i = 0; i < lista.size(); i++) {
                    if (lista.get(i).getId() == producto.getCategoria()) {
                        cmbCategoria.setSelection(i);
                        break;
                    }
                }
            }
        });

        mViewModel.getImagenProducto().observe(getViewLifecycleOwner(), archivo -> {
            if (archivo != null && producto != null && archivo.getIdProducto() == producto.getId()) {
                try {
                    byte[] decodedString = Base64.decode(archivo.getDatos(), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    imgProducto.setImageBitmap(decodedByte);
                } catch (Exception e) {
                    imgProducto.setImageResource(R.drawable.ic_launcher_background);
                }
            }
        });

        // Observador de mensajes del servidor
        mViewModel.getMensaje().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) {
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                if (msg.toLowerCase().contains("exitosamente") || msg.toLowerCase().contains("éxito")) {
                    mViewModel.mensaje.setValue(null); // Limpiar mensaje para evitar bucles
                    regresarAlListado();
                }
            }
        });
    }

    private void activarModoEdicion(boolean activar) {
        modoEdicion = activar;
        etNombre.setEnabled(activar);
        etPrecio.setEnabled(activar);
        etStock.setEnabled(activar);
        etDesc.setEnabled(activar);
        cmbCategoria.setEnabled(activar);
        swDisponible.setEnabled(activar);

        btnCambiarFoto.setVisibility(activar ? View.VISIBLE : View.GONE);
        btnCancelar.setVisibility(activar ? View.VISIBLE : View.GONE);
        btnAccion.setText(activar ? "Guardar Cambios" : "Modificar");
    }

    private void ejecutarActualizacion() {
        String nombre = etNombre.getText().toString().trim();
        String descripcion = etDesc.getText().toString().trim();
        String precioStr = etPrecio.getText().toString().trim();
        String stockStr = etStock.getText().toString().trim();

        if (validarCampos(nombre, precioStr, stockStr)) {
            try {
                producto.setNombre(nombre);
                producto.setDescripcion(descripcion);
                producto.setPrecio(new BigDecimal(precioStr));
                producto.setUnidades(Integer.parseInt(stockStr));
                producto.setDisponible(swDisponible.isChecked());

                CategoriaDTO cat = (CategoriaDTO) cmbCategoria.getSelectedItem();
                if (cat != null) producto.setCategoria(cat.getId());

                mViewModel.actualizarProducto(producto, nuevaImagenUri, requireContext());
            } catch (Exception e) {
                Toast.makeText(getContext(), "Error en el formato de los datos", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean validarCampos(String nombre, String precio, String stock) {
        if (nombre.isEmpty() || precio.isEmpty() || stock.isEmpty()) {
            Toast.makeText(getContext(), "Completa los campos obligatorios", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (nombre.length() < 3) {
            etNombre.setError("Nombre muy corto");
            return false;
        }
        return true;
    }

    private void limpiarErrores() {
        etNombre.setError(null);
        etPrecio.setError(null);
        etStock.setError(null);
    }

    private void regresarAlListado() {
        if (isAdded()) {
            getParentFragmentManager().popBackStack();
        }
    }
}