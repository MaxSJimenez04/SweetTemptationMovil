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

    // Para abrir la galeria de fotos
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
        mViewModel.cargarCategorias();
        llenarDatosIniciales();

        btnAccion.setOnClickListener(v -> {
            if (!modoEdicion) {
                activarModoEdicion(true);
            } else {
                ejecutarActualizacion();
            }
        });

        btnCancelar.setOnClickListener(v -> {
            activarModoEdicion(false);

            etNombre.setError(null);
            etPrecio.setError(null);
            etStock.setError(null);

            llenarDatosIniciales();

            nuevaImagenUri = null;
        });

        btnCambiarFoto.setOnClickListener(v -> mGetContent.launch("image/*"));
        btnRegresar.setOnClickListener(v -> getParentFragmentManager().popBackStack());
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
        etNombre.setText(producto.getNombre());
        etPrecio.setText(producto.getPrecio().toString());
        etStock.setText(String.valueOf(producto.getUnidades()));
        etDesc.setText(producto.getDescripcion());
        swDisponible.setChecked(producto.isDisponible());

        mViewModel.getImagenProducto().observe(getViewLifecycleOwner(), archivo -> {
            if (archivo != null && archivo.getIdProducto() == producto.getId()) {
                byte[] decodedString = Base64.decode(archivo.getDatos(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imgProducto.setImageBitmap(decodedByte);
            }
        });
        mViewModel.obtenerRutaArchivo(producto.getId());
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

        mViewModel.getMensaje().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) {
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();

                if (msg.contains("exitosamente")) {
                    getParentFragmentManager().popBackStack();

                    mViewModel.mensaje.setValue(null);
                }
            }
        });
    }

    // TODO - Seguir trabajando en las posibles validaciones
    private void ejecutarActualizacion() {
        String nombre = etNombre.getText().toString().trim();
        String descripcion = etDesc.getText().toString().trim();
        String precioStr = etPrecio.getText().toString().trim();
        String stockStr = etStock.getText().toString().trim();

        if (nombre.isEmpty() || precioStr.isEmpty() || stockStr.isEmpty()) {
            Toast.makeText(getContext(), "Por favor, completa los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!precioStr.matches("^\\d+(\\.\\d{1,2})?$")) {
            etPrecio.setError("Formato de precio inválido (máximo 2 decimales)");
            etPrecio.requestFocus();
            return;
        }

        if (nombre.length() < 3 || nombre.length() > 50) {
            etNombre.setError("El nombre debe tener entre 3 y 50 caracteres");
            etNombre.requestFocus();
            return;
        }

        try {
            BigDecimal precio = new BigDecimal(precioStr);
            int unidades = Integer.parseInt(stockStr);

            if (precio.compareTo(BigDecimal.ZERO) <= 0) {
                etPrecio.setError("El precio debe ser mayor a 0");
                etPrecio.requestFocus();
                return;
            }

            if (unidades < 0) {
                etStock.setError("El stock no puede ser negativo");
                etStock.requestFocus();
                return;
            }

            producto.setNombre(nombre);
            producto.setDescripcion(descripcion);
            producto.setPrecio(precio);
            producto.setUnidades(unidades);
            producto.setDisponible(swDisponible.isChecked());

            CategoriaDTO cat = (CategoriaDTO) cmbCategoria.getSelectedItem();
            if (cat != null) producto.setCategoria(cat.getId());

            mViewModel.actualizarProducto(producto, nuevaImagenUri, requireContext());

        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Error en el formato de los números", Toast.LENGTH_SHORT).show();
        }
    }
}
