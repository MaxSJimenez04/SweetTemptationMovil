package com.example.sweettemptation.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.sweettemptation.R;
import com.example.sweettemptation.auth.TokenStorage;
import com.example.sweettemptation.databinding.FragmentInicioAdminBinding;

public class InicioAdminFragment extends Fragment {

    private FragmentInicioAdminBinding binding;
    private TokenStorage tokenStorage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentInicioAdminBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tokenStorage = new TokenStorage(requireContext());

        cargarDatosUsuario();
        configurarListeners();
    }

    private void cargarDatosUsuario() {
        SharedPreferences prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String nombre = prefs.getString("user_nombre", "Usuario");
        String rol = prefs.getString("user_rol", "Administrador");

        binding.tvSaludo.setText("¡Hola, " + nombre + "!");
        binding.tvRol.setText(rol);
    }

    private void configurarListeners() {
        binding.btnCatalogo.setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.fragmentCatalogoProductos)
        );

        binding.btnEstadisticas.setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.fragmentSeleccionarEstadisticas)
        );

        binding.btnCuentas.setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.fragmentGestionUsuarios)
        );

        binding.btnPerfil.setOnClickListener(v ->
                Toast.makeText(requireContext(), "Perfil próximamente", Toast.LENGTH_SHORT).show()
        );

        binding.btnCerrarSesion.setOnClickListener(v ->
                confirmarCerrarSesion()
        );
    }

    private void confirmarCerrarSesion() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Cerrar sesión")
                .setMessage("¿Estás seguro que deseas cerrar sesión?")
                .setPositiveButton("Sí", (dialog, which) -> cerrarSesion())
                .setNegativeButton("No", null)
                .show();
    }

    private void cerrarSesion() {
        tokenStorage.clear();

        SharedPreferences prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();

        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

