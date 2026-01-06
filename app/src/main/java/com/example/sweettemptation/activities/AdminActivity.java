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

        // Configurar barras del sistema con modo inmersivo
        configurarBarrasSistema();

        tokenStorage = new TokenStorage(this);

        // Mostrar datos del usuario
        mostrarDatosUsuario();

        // Configurar botones
        configurarBotones();

        // Escuchar cuando regresas de un fragmento para volver a mostrar la bienvenida
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                mostrarBienvenida(true);
            }
        });
    }

    private void mostrarDatosUsuario() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String nombre = prefs.getString("user_nombre", "Administrador");
        String rol = prefs.getString("user_rol", "Administrador");

        binding.tvBienvenida.setText("Bienvenido, " + nombre);
        binding.tvRol.setText("Rol: " + rol);
    }

    private void configurarBotones() {
        // Botón Gestionar Cuentas
        binding.btnUsuarios.setOnClickListener(v -> {
            Intent intent = new Intent(this, UsuariosActivity.class);
            startActivity(intent);
        });

        // Botón Gestión de Productos
        binding.btnProductos.setOnClickListener(v -> {
            mostrarBienvenida(false);
            cargarFragmento(new ProductoFragment());
        });

        // Botón Ver Estadísticas
        binding.btnEstadisticas.setOnClickListener(v -> {
            mostrarBienvenida(false);
            cargarFragmento(new estadisticasProducto());
        });

        // Botón Cerrar Sesión
        binding.btnLogout.setOnClickListener(v -> confirmarCerrarSesion());
    }

    private void cargarFragmento(Fragment fragmento) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container, fragmento)
                .addToBackStack(null)
                .commit();
    }

    private void mostrarBienvenida(boolean mostrar) {
        int visibilidadBienvenida = mostrar ? View.VISIBLE : View.GONE;
        int visibilidadFragment = mostrar ? View.GONE : View.VISIBLE;

        // Elementos de bienvenida
        binding.tvTitulo.setVisibility(visibilidadBienvenida);
        binding.tvBienvenida.setVisibility(visibilidadBienvenida);
        binding.tvRol.setVisibility(visibilidadBienvenida);
        binding.layoutBotones.setVisibility(visibilidadBienvenida);

        // Contenedor de fragmentos
        binding.fragmentContainer.setVisibility(visibilidadFragment);
    }

    private void confirmarCerrarSesion() {
        new AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Estás seguro que deseas cerrar sesión?")
                .setPositiveButton("Sí", (dialog, which) -> cerrarSesion())
                .setNegativeButton("No", null)
                .show();
    }

    private void cerrarSesion() {
        // Limpiar token
        tokenStorage.clear();

        // Limpiar datos de usuario
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        prefs.edit().clear().apply();

        // Navegar a Login y limpiar el stack
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Si hay fragmentos en el back stack, dejar que el sistema los maneje
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    private void configurarBarrasSistema() {
        Window window = getWindow();
        
        // Establecer colores de las barras
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
        
        // Modo inmersivo - barras se ocultan y aparecen con swipe
        View decorView = window.getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
