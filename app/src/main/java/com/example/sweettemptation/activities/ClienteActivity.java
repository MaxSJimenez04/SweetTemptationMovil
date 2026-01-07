package com.example.sweettemptation.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.sweettemptation.R;
import com.example.sweettemptation.auth.TokenStorage;
import com.example.sweettemptation.databinding.ActivityClienteBinding;

public class ClienteActivity extends AppCompatActivity {

    private ActivityClienteBinding binding;
    private TokenStorage tokenStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityClienteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tokenStorage = new TokenStorage(this);

        configurarMenus();
        configurarFab();
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host);

        NavController navController = navHostFragment.getNavController();
        // Cargar el catálogo por defecto al iniciar la actividad
        if (savedInstanceState == null) {
            navController.navigate(R.id.fragmentProductosCliente);
        }
    }

    private void configurarMenus() {
        binding.abTop.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.btnCuenta) {
                mostrarMenuCuenta();
                return true;
            }
            return false;
        });

        // Menú inferior
        binding.abBottom.setOnMenuItemClickListener(item -> {
            NavHostFragment navHostFragment =
                    (NavHostFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.nav_host);

            NavController navController = navHostFragment.getNavController();
            int id = item.getItemId();
            if (id == R.id.btnHistorial) {
                Toast.makeText(this, "Historial próximamente", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.btnCarrito) {
                navController.navigate(R.id.fragmentPedido);
                return true;
            } else if (id == R.id.btnProductos) {
                navController.navigate(R.id.fragmentProductosCliente);
                return true;
            }
            return false;
        });
    }

    private void configurarFab() {
        binding.btnMenu.setOnClickListener(v -> mostrarMenuPrincipal());
    }

    private void mostrarMenuPrincipal() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String nombre = prefs.getString("user_nombre", "Usuario");

        String[] opciones = {"Ver Catálogo", "Mis Pedidos", "Mi Carrito", "Mi Perfil", "Cerrar Sesión"};

        new AlertDialog.Builder(this)
                .setTitle("Hola, " + nombre)
                .setItems(opciones, (dialog, which) -> {
                    NavHostFragment navHostFragment =
                            (NavHostFragment) getSupportFragmentManager()
                                    .findFragmentById(R.id.nav_host);

                    NavController navController = navHostFragment.getNavController();
                    switch (which) {

                        case 0:
                            navController.navigate(R.id.fragmentProductosCliente);
                            break;
                        case 1:
                            Toast.makeText(this, "Pedidos próximamente", Toast.LENGTH_SHORT).show();
                            break;
                        case 2: // Mi Carrito
                            navController.navigate(R.id.fragmentPedido);
                            break;
                        case 3:
                            Toast.makeText(this, "Perfil próximamente", Toast.LENGTH_SHORT).show();
                            break;
                        case 4:
                            confirmarCerrarSesion();
                            break;
                    }
                })
                .show();
    }


    private void mostrarMenuCuenta() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String nombre = prefs.getString("user_nombre", "Usuario");
        String[] opciones = {"Mi perfil", "Cerrar sesión"};
        new AlertDialog.Builder(this)
                .setTitle("Hola, " + nombre)
                .setItems(opciones, (dialog, which) -> {
                    if (which == 0) Toast.makeText(this, "Perfil próximamente", Toast.LENGTH_SHORT).show();
                    else if (which == 1) confirmarCerrarSesion();
                }).show();
    }

    private void confirmarCerrarSesion() {
        new AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Estás seguro que deseas cerrar sesión?")
                .setPositiveButton("Sí", (dialog, which) -> cerrarSesion())
                .setNegativeButton("No", null)
                .show();
    }

    public void cerrarSesion() {
        tokenStorage.clear();
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        prefs.edit().clear().apply();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}