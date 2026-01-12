package com.example.sweettemptation.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.sweettemptation.R;
import com.example.sweettemptation.databinding.FragmentGestionUsuariosBinding;
import com.example.sweettemptation.dto.UsuarioResponse;
import com.example.sweettemptation.interfaces.UsuarioApi;
import com.example.sweettemptation.network.ApiCliente;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GestionUsuariosFragment extends Fragment implements UsuarioAdapter.OnUsuarioClickListener {

    private FragmentGestionUsuariosBinding binding;
    private UsuarioApi usuarioApi;
    private UsuarioAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentGestionUsuariosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ApiCliente.init(requireContext());
        usuarioApi = ApiCliente.getInstance().retrofit().create(UsuarioApi.class);

        configurarRecyclerView();
        configurarListeners();
    }

    private void configurarRecyclerView() {
        adapter = new UsuarioAdapter(this);
        binding.recyclerUsuarios.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerUsuarios.setAdapter(adapter);
    }

    private void configurarListeners() {
        binding.btnRegresar.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        binding.fabAgregar.setOnClickListener(v -> abrirFormulario(null, false));
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarUsuarios();
    }

    private void cargarUsuarios() {
        mostrarCarga(true);

        usuarioApi.getUsuarios().enqueue(new Callback<List<UsuarioResponse>>() {
            @Override
            public void onResponse(Call<List<UsuarioResponse>> call, Response<List<UsuarioResponse>> response) {
                mostrarCarga(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<UsuarioResponse> usuarios = response.body();
                    adapter.setUsuarios(usuarios);
                    actualizarVistaVacia(usuarios.isEmpty());
                } else {
                    String error = "Error al cargar usuarios";
                    if (response.code() == 401 || response.code() == 403) {
                        error = "Sesión expirada";
                    }
                    Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<UsuarioResponse>> call, Throwable t) {
                mostrarCarga(false);
                Toast.makeText(requireContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void actualizarVistaVacia(boolean vacio) {
        binding.layoutVacio.setVisibility(vacio ? View.VISIBLE : View.GONE);
        binding.recyclerUsuarios.setVisibility(vacio ? View.GONE : View.VISIBLE);
    }

    private void mostrarCarga(boolean mostrar) {
        binding.progressBar.setVisibility(mostrar ? View.VISIBLE : View.GONE);
    }

    private boolean esUltimoAdmin(UsuarioResponse usuario) {
        if (usuario.getIdRol() != 1) return false;

        int contadorAdmins = 0;
        for (UsuarioResponse u : adapter.getUsuarios()) {
            if (u.getIdRol() == 1) {
                contadorAdmins++;
            }
        }
        return contadorAdmins <= 1;
    }

    @Override
    public void onEditarClick(UsuarioResponse usuario) {
        boolean ultimoAdmin = esUltimoAdmin(usuario);
        abrirFormulario(usuario, ultimoAdmin);
    }

    @Override
    public void onEliminarClick(UsuarioResponse usuario) {
        if (esUltimoAdmin(usuario)) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Acción no permitida")
                    .setMessage("No puedes eliminar al único administrador del sistema. Primero crea otro administrador.")
                    .setIcon(R.drawable.icon_eliminar)
                    .setPositiveButton("Entendido", null)
                    .show();
            return;
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar usuario")
                .setMessage("¿Estás seguro de eliminar a " + usuario.getNombreCompleto() + "?\n\nEsta acción no se puede deshacer.")
                .setPositiveButton("Eliminar", (dialog, which) -> eliminarUsuario(usuario))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void abrirFormulario(UsuarioResponse usuario, boolean esUltimoAdmin) {
        FormularioUsuarioFragment fragment = FormularioUsuarioFragment.newInstance(usuario, esUltimoAdmin);
        
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(R.id.nav_host, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void eliminarUsuario(UsuarioResponse usuario) {
        mostrarCarga(true);

        usuarioApi.deleteUsuario(usuario.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                mostrarCarga(false);

                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Usuario eliminado", Toast.LENGTH_SHORT).show();
                    cargarUsuarios();
                } else {
                    String error = "Error al eliminar";
                    if (response.code() == 401 || response.code() == 403) {
                        error = "Sesión expirada";
                    } else if (response.code() == 400) {
                        error = "No se puede eliminar este usuario";
                    }
                    Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                mostrarCarga(false);
                Toast.makeText(requireContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

