package com.example.sweettemptation.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.sweettemptation.R;
import com.example.sweettemptation.auth.TokenStorage;
import com.example.sweettemptation.databinding.ActivityAdminBinding;

public class AdminActivity extends AppCompatActivity {

    private ActivityAdminBinding binding;
    private TokenStorage tokenStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Inicializar TokenStorage (necesario para cerrar sesión)
        tokenStorage = new TokenStorage(this);
        
        configurarMenus();
        configurarFab();
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
            int id = item.getItemId();
            NavHostFragment navHostFragment =
                    (NavHostFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.nav_host);

            NavController navController = navHostFragment.getNavController();
            if (id == R.id.btnEstadisticas) {
                navController.navigate(R.id.fragmentSeleccionarEstadisticas);
                return true;
            } else if (id == R.id.btnCuentas) {
                //TODO: cambiar a fragment de gestionar cuentas
                return true;
            } else if (id == R.id.btnProductos) {
                navController.navigate(R.id.fragmentCatalogoProductos);
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

        String[] opciones = {"Ver Catálogo", "Ver estadísticas", "Cuentas", "Mi Perfil", "Cerrar Sesión"};

        new AlertDialog.Builder(this)
                .setTitle("Hola, " + nombre)
                .setItems(opciones, (dialog, which) -> {
                    NavHostFragment navHostFragment =
                            (NavHostFragment) getSupportFragmentManager()
                                    .findFragmentById(R.id.nav_host);

                    NavController navController = navHostFragment.getNavController();
                    switch (which) {
                        case 0:
                            navController.navigate(R.id.fragmentCatalogoProductos);
                            break;
                        case 1:
                            navController.navigate(R.id.fragmentSeleccionarEstadisticas);
                            break;
                        case 2:
                            //TODO: cambiar a gestionar cuentas
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
