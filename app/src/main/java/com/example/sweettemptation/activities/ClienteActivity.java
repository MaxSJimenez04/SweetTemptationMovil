package com.example.sweettemptation.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
                Toast.makeText(this, "Historial de pedidos", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.btnCarrito) {
                Toast.makeText(this, "Carrito de compras", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.btnProductos) {
                Toast.makeText(this, "Catálogo de productos", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
    }

    private void configurarFab() {
        binding.btnMenu.setOnClickListener(v -> {
            // Mostrar menú principal con opciones
            mostrarMenuPrincipal();
        });
    }

    private void mostrarMenuPrincipal() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String nombre = prefs.getString("user_nombre", "Usuario");
        
        String[] opciones = {"Ver Catálogo", "Mis Pedidos", "Mi Carrito", "Mi Perfil", "Cerrar Sesión"};
        
        new AlertDialog.Builder(this)
                .setTitle("Hola, " + nombre)
                .setItems(opciones, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            Toast.makeText(this, "Catálogo próximamente", Toast.LENGTH_SHORT).show();
                            break;
                        case 1:
                            Toast.makeText(this, "Pedidos próximamente", Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            Toast.makeText(this, "Carrito próximamente", Toast.LENGTH_SHORT).show();
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
                    if (which == 0) {
                        Toast.makeText(this, "Perfil próximamente", Toast.LENGTH_SHORT).show();
                    } else if (which == 1) {
                        confirmarCerrarSesion();
                    }
                })
                .show();
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
        // Limpiar token
        tokenStorage.clear();
        
        // Limpiar datos de usuario
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        prefs.edit().clear().apply();
        
        // Ir al login
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

