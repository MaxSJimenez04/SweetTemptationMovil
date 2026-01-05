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

        // Cargar el catálogo por defecto al iniciar la actividad
        if (savedInstanceState == null) {
            reemplazarFragmento(new CatalogoProductosClienteFragment());
        }
    }

    private void configurarMenus() {
        // Menú superior - Cuenta
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
            if (id == R.id.btnHistorial) {
                Toast.makeText(this, "Historial próximamente", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.btnCarrito) {
                reemplazarFragmento(new pedido()); // Cambiar a tu fragmento de carrito
                return true;
            } else if (id == R.id.btnProductos) {
                // ENLACE A TU CATÁLOGO
                reemplazarFragmento(new CatalogoProductosClienteFragment());
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
                    switch (which) {
                        case 0: // Ver Catálogo
                            reemplazarFragmento(new CatalogoProductosClienteFragment());
                            break;
                        case 1:
                            Toast.makeText(this, "Pedidos próximamente", Toast.LENGTH_SHORT).show();
                            break;
                        case 2: // Mi Carrito
                            reemplazarFragmento(new pedido());
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

    private void reemplazarFragmento(Fragment fragmento) {
        Fragment actual = getSupportFragmentManager().findFragmentById(R.id.nav_host);

        // Solo reemplazamos si el fragmento nuevo es distinto al que ya se ve
        if (actual == null || !actual.getClass().equals(fragmento.getClass())) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(R.id.nav_host, fragmento)
                    .addToBackStack(null)
                    .commit();
        }
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