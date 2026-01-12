package com.example.sweettemptation.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sweettemptation.R;
import com.example.sweettemptation.databinding.FragmentFormularioUsuarioBinding;
import com.example.sweettemptation.dto.UsuarioRequest;
import com.example.sweettemptation.dto.UsuarioResponse;
import com.example.sweettemptation.interfaces.UsuarioApi;
import com.example.sweettemptation.network.ApiCliente;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormularioUsuarioFragment extends Fragment {

    private static final String ARG_USUARIO = "usuario";
    private static final String ARG_ES_ULTIMO_ADMIN = "es_ultimo_admin";

    private FragmentFormularioUsuarioBinding binding;
    private UsuarioApi usuarioApi;

    private UsuarioResponse usuarioEditar = null;
    private boolean esEdicion = false;
    private boolean esUltimoAdmin = false;

    private String[] roles = {"Administrador", "Empleado", "Cliente"};
    private int[] rolesIds = {1, 2, 3};

    public static FormularioUsuarioFragment newInstance(UsuarioResponse usuario, boolean esUltimoAdmin) {
        FormularioUsuarioFragment fragment = new FormularioUsuarioFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_USUARIO, usuario);
        args.putBoolean(ARG_ES_ULTIMO_ADMIN, esUltimoAdmin);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFormularioUsuarioBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ApiCliente.init(requireContext());
        usuarioApi = ApiCliente.getInstance().retrofit().create(UsuarioApi.class);

        configurarSpinner();
        configurarModo();
        configurarListeners();
    }

    private void configurarSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerRol.setAdapter(adapter);
        binding.spinnerRol.setSelection(2);
    }

    private void configurarModo() {
        if (getArguments() != null && getArguments().containsKey(ARG_USUARIO)) {
            usuarioEditar = (UsuarioResponse) getArguments().getSerializable(ARG_USUARIO);
            esUltimoAdmin = getArguments().getBoolean(ARG_ES_ULTIMO_ADMIN, false);
        }

        if (usuarioEditar != null) {
            esEdicion = true;
            binding.tvTitulo.setText("Editar Usuario");
            cargarDatosUsuario();

            binding.tvLabelContrasena.setText("Contraseña (opcional)");
            binding.etContrasena.setHint("Dejar vacío para no cambiar");

            if (esUltimoAdmin && usuarioEditar.getIdRol() == 1) {
                binding.spinnerRol.setEnabled(false);
                binding.layoutAvisoAdmin.setVisibility(View.VISIBLE);
            }
        } else {
            binding.tvTitulo.setText("Nuevo Usuario");
        }
    }

    private void cargarDatosUsuario() {
        if (usuarioEditar == null) return;

        binding.etUsuario.setText(usuarioEditar.getUsuario());
        binding.etNombre.setText(usuarioEditar.getNombre());
        binding.etApellidos.setText(usuarioEditar.getApellidos());
        binding.etCorreo.setText(usuarioEditar.getCorreo());
        binding.etTelefono.setText(usuarioEditar.getTelefono());
        binding.etDireccion.setText(usuarioEditar.getDireccion());

        for (int i = 0; i < rolesIds.length; i++) {
            if (rolesIds[i] == usuarioEditar.getIdRol()) {
                binding.spinnerRol.setSelection(i);
                break;
            }
        }
    }

    private void configurarListeners() {
        binding.btnRegresar.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        binding.btnGuardar.setOnClickListener(v -> {
            if (validarFormulario()) {
                guardarUsuario();
            }
        });
    }

    private boolean validarFormulario() {
        String usuario = binding.etUsuario.getText().toString().trim();
        String nombre = binding.etNombre.getText().toString().trim();
        String apellidos = binding.etApellidos.getText().toString().trim();
        String correo = binding.etCorreo.getText().toString().trim();
        String contrasena = binding.etContrasena.getText().toString().trim();
        String telefono = binding.etTelefono.getText().toString().trim();

        if (usuario.isEmpty()) {
            binding.etUsuario.setError("El usuario es requerido");
            binding.etUsuario.requestFocus();
            return false;
        }

        if (usuario.length() < 4) {
            binding.etUsuario.setError("Mínimo 4 caracteres");
            binding.etUsuario.requestFocus();
            return false;
        }

        if (nombre.isEmpty()) {
            binding.etNombre.setError("El nombre es requerido");
            binding.etNombre.requestFocus();
            return false;
        }

        if (apellidos.isEmpty()) {
            binding.etApellidos.setError("Los apellidos son requeridos");
            binding.etApellidos.requestFocus();
            return false;
        }

        if (correo.isEmpty()) {
            binding.etCorreo.setError("El correo es requerido");
            binding.etCorreo.requestFocus();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            binding.etCorreo.setError("Correo inválido");
            binding.etCorreo.requestFocus();
            return false;
        }

        if (!esEdicion && contrasena.isEmpty()) {
            binding.etContrasena.setError("La contraseña es requerida");
            binding.etContrasena.requestFocus();
            return false;
        }

        if (!contrasena.isEmpty() && contrasena.length() < 6) {
            binding.etContrasena.setError("Mínimo 6 caracteres");
            binding.etContrasena.requestFocus();
            return false;
        }

        if (telefono.isEmpty()) {
            binding.etTelefono.setError("El teléfono es requerido");
            binding.etTelefono.requestFocus();
            return false;
        }

        if (telefono.length() < 10) {
            binding.etTelefono.setError("Mínimo 10 dígitos");
            binding.etTelefono.requestFocus();
            return false;
        }

        return true;
    }

    private void guardarUsuario() {
        mostrarCarga(true);

        String usuario = binding.etUsuario.getText().toString().trim();
        String nombre = binding.etNombre.getText().toString().trim();
        String apellidos = binding.etApellidos.getText().toString().trim();
        String correo = binding.etCorreo.getText().toString().trim();
        String contrasena = binding.etContrasena.getText().toString().trim();
        String telefono = binding.etTelefono.getText().toString().trim();
        String direccion = binding.etDireccion.getText().toString().trim();
        int idRol = rolesIds[binding.spinnerRol.getSelectedItemPosition()];

        UsuarioRequest request = new UsuarioRequest(
                usuario, nombre, apellidos, correo,
                contrasena.isEmpty() ? null : contrasena,
                telefono, direccion, idRol
        );

        Call<UsuarioResponse> call;
        if (esEdicion) {
            call = usuarioApi.updateUsuario(usuarioEditar.getId(), request);
        } else {
            call = usuarioApi.createUsuario(request);
        }

        call.enqueue(new Callback<UsuarioResponse>() {
            @Override
            public void onResponse(Call<UsuarioResponse> call, Response<UsuarioResponse> response) {
                mostrarCarga(false);

                if (response.isSuccessful()) {
                    String mensaje = esEdicion ? "Usuario actualizado" : "Usuario creado";
                    Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show();
                    
                    if (getActivity() != null) {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                } else {
                    String error = "Error al guardar";
                    if (response.code() == 400) {
                        error = "Datos inválidos o usuario/correo duplicado";
                    } else if (response.code() == 401 || response.code() == 403) {
                        error = "Sesión expirada";
                    }
                    Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UsuarioResponse> call, Throwable t) {
                mostrarCarga(false);
                Toast.makeText(requireContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void mostrarCarga(boolean mostrar) {
        binding.progressBar.setVisibility(mostrar ? View.VISIBLE : View.GONE);
        binding.btnGuardar.setEnabled(!mostrar);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

