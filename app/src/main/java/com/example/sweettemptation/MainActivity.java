package com.example.sweettemptation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;

import com.example.sweettemptation.activities.LoginActivity;
import com.example.sweettemptation.activities.ProductoFragment;
import com.example.sweettemptation.activities.SeleccionarEstadisticasFragment;
import com.example.sweettemptation.auth.TokenStorage;
import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    private TokenStorage tokenStorage;
    private MaterialButton btnGestionarProductos;
    private MaterialButton btnSeleccionarEstadisticas;
    private View layoutContenidoPrincipal; // Para agrupar y ocultar la bienvenida

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tokenStorage = new TokenStorage(this);

        // Inicializar vistas
        btnGestionarProductos = findViewById(R.id.btnGestionarProductos);
        btnSeleccionarEstadisticas = findViewById(R.id.btnSeleccionarEstadisticas);
        Button btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        // Mostrar datos y validar el botón por Rol
        mostrarDatosUsuario();

        // Configurar botones
        btnGestionarProductos.setOnClickListener(v -> abrirGestionProductos());
        btnCerrarSesion.setOnClickListener(v -> confirmarCerrarSesion());
        btnSeleccionarEstadisticas.setOnClickListener(v -> abrirSeleccionarEstadisticas());

        // Escuchar cuando regresas de un fragmento para volver a mostrar la bienvenida
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                alternarVisibilidadBienvenida(true);
            }
        });
    }

    /*
    ==== Metodo original ===

    private void mostrarDatosUsuario() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String nombre = prefs.getString("user_nombre", "Usuario");
        String rol = prefs.getString("user_rol", "");

        TextView tvBienvenida = findViewById(R.id.tvBienvenida);
        TextView tvRol = findViewById(R.id.tvRol);

        tvBienvenida.setText("Bienvenido, " + nombre);
        tvRol.setText("Rol: " + rol);

        // SOLO MOSTRAR EL BOTÓN SI EL USUARIO ES ADMIN
        if ("ADMIN".equalsIgnoreCase(rol)) {
            btnGestionarProductos.setVisibility(View.VISIBLE);
        } else {
            btnGestionarProductos.setVisibility(View.GONE);
        }
    }*/

    private void mostrarDatosUsuario() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String nombre = prefs.getString("user_nombre", "Usuario");
        String rol = prefs.getString("user_rol", "");

        TextView tvBienvenida = findViewById(R.id.tvBienvenida);
        TextView tvRol = findViewById(R.id.tvRol);

        tvBienvenida.setText("Bienvenido, " + nombre);
        tvRol.setText("Rol: " + rol);

        if (rol.equalsIgnoreCase("ADMIN") || rol.equalsIgnoreCase("Administrador")) {
            btnGestionarProductos.setVisibility(View.VISIBLE);
        } else {
            btnGestionarProductos.setVisibility(View.GONE);
        }
    }

    private void abrirGestionProductos() {
        // 1. Ocultar la UI de bienvenida
        alternarVisibilidadBienvenida(false);

        // 2. Cargar el Fragmento de la lista de productos
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new ProductoFragment())
                .addToBackStack(null) // Esto permite usar el botón "atrás" del cel
                .commit();
    }

    private void abrirSeleccionarEstadisticas(){
        alternarVisibilidadBienvenida(false);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SeleccionarEstadisticasFragment()).addToBackStack(null)
                .commit();
    }

    private void alternarVisibilidadBienvenida(boolean mostrar) {
        int visibilidad = mostrar ? View.VISIBLE : View.GONE;
        findViewById(R.id.tvBienvenida).setVisibility(visibilidad);
        findViewById(R.id.tvRol).setVisibility(visibilidad);
        findViewById(R.id.tvMensaje).setVisibility(visibilidad);
        findViewById(R.id.btnCerrarSesion).setVisibility(visibilidad);

        // El botón de productos solo se muestra si el rol es ADMIN al volver
        if (mostrar) {
            mostrarDatosUsuario();
        } else {
            btnGestionarProductos.setVisibility(View.GONE);
        }
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
        tokenStorage.clear();
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        prefs.edit().clear().apply();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}