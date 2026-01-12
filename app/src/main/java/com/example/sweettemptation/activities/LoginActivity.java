package com.example.sweettemptation.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.sweettemptation.MainActivity;
import android.content.SharedPreferences;
import com.example.sweettemptation.R;
import com.example.sweettemptation.auth.TokenStorage;
import com.example.sweettemptation.databinding.ActivityLoginBinding;
import com.example.sweettemptation.dto.LoginRequest;
import com.example.sweettemptation.dto.LoginResponse;
import com.example.sweettemptation.interfaces.AuthApi;
import com.example.sweettemptation.network.ApiCliente;
import com.example.sweettemptation.utils.Constantes;
import com.example.sweettemptation.utils.UserSession;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements RecuperarPasswordFragment.OnRecuperarPasswordListener {

    private ActivityLoginBinding binding;
    private TokenStorage tokenStorage;
    private AuthApi authApi;
    private boolean fragmentRecuperarVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Inicializar ViewBinding
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Configurar barras del sistema
        configurarBarrasSistema();

        // Inicializar ApiCliente si no está inicializado
        ApiCliente.init(this);
        
        // Obtener instancias
        tokenStorage = new TokenStorage(this);
        authApi = ApiCliente.getInstance().retrofit().create(AuthApi.class);

        // Verificar si ya hay un token válido
        if (tokenStorage.getToken() != null) {
            navegarAMain();
            return;
        }

        // Configurar listeners
        configurarListeners();
        
        // Manejar botón back del sistema
        configurarOnBackPressed();
    }

    private void configurarListeners() {
        // Botón de login
        binding.btnLogin.setOnClickListener(v -> intentarLogin());

        // Link de recuperar contraseña
        binding.tvRecuperar.setOnClickListener(v -> mostrarRecuperarPassword());
    }

    private void mostrarRecuperarPassword() {
        fragmentRecuperarVisible = true;
        
        // Ocultar login y mostrar contenedor del fragment
        binding.loginContainer.setVisibility(View.GONE);
        binding.fragmentContainer.setVisibility(View.VISIBLE);
        
        // Cargar el fragment
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragmentContainer, new RecuperarPasswordFragment())
                .commit();
    }

    @Override
    public void onCerrarRecuperarPassword() {
        ocultarRecuperarPassword();
    }

    private void ocultarRecuperarPassword() {
        fragmentRecuperarVisible = false;
        
        // Remover el fragment
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .remove(fragment)
                    .commit();
        }
        
        // Mostrar login y ocultar contenedor del fragment
        binding.fragmentContainer.setVisibility(View.GONE);
        binding.loginContainer.setVisibility(View.VISIBLE);
    }

    private void configurarOnBackPressed() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (fragmentRecuperarVisible) {
                    // Si el fragment está visible, cerrarlo
                    ocultarRecuperarPassword();
                } else {
                    // Comportamiento normal (salir de la app)
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }

    private void intentarLogin() {
        String usuario = binding.etUsuario.getText().toString().trim();
        String contrasena = binding.etPassword.getText().toString().trim();

        // Validar campos vacíos
        if (usuario.isEmpty()) {
            binding.etUsuario.setError("Ingresa tu usuario");
            binding.etUsuario.requestFocus();
            return;
        }

        if (contrasena.isEmpty()) {
            binding.etPassword.setError("Ingresa tu contraseña");
            binding.etPassword.requestFocus();
            return;
        }

        // Mostrar loading
        mostrarLoading(true);

        // Crear request y llamar a la API
        LoginRequest request = new LoginRequest(usuario, contrasena);
        
        authApi.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                mostrarLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    // Login exitoso
                    LoginResponse loginResponse = response.body();
                    
                    // Guardar token y datos del usuario
                    tokenStorage.saveToken(loginResponse.getToken());
                    UserSession.save(LoginActivity.this, loginResponse);

                    // Navegar según el rol
                    navegarSegunRol(loginResponse.getRol());
                    
                } else if (response.code() == 401 || response.code() == 403) {
                    // Credenciales incorrectas
                    Toast.makeText(LoginActivity.this, 
                            "Credenciales incorrectas", 
                            Toast.LENGTH_LONG).show();
                } else {
                    // Otro error del servidor
                    Toast.makeText(LoginActivity.this, 
                            Constantes.MENSAJE_FALLA_SERVIDOR, 
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                mostrarLoading(false);
                
                // Error de conexión
                Toast.makeText(LoginActivity.this, 
                        Constantes.MENSAJE_SIN_CONEXION, 
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void mostrarLoading(boolean mostrar) {
        binding.btnLogin.setEnabled(!mostrar);
        binding.progressBar.setVisibility(mostrar ? View.VISIBLE : View.GONE);
        binding.etUsuario.setEnabled(!mostrar);
        binding.etPassword.setEnabled(!mostrar);
    }

    private void navegarSegunRol(String rol) {
        Intent intent;
        
        if ("ADMIN".equalsIgnoreCase(rol) || "Administrador".equalsIgnoreCase(rol)) {
            // Administrador va a AdminActivity
            intent = new Intent(this, AdminActivity.class);
        } else if ("CLIENTE".equalsIgnoreCase(rol)) {
            // Cliente va a ClienteActivity
            intent = new Intent(this, ClienteActivity.class);
        } else {
            // Empleado va a MainActivity
            intent = new Intent(this, MainActivity.class);
        }
        
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void navegarAMain() {
        // Verificar rol guardado para navegar correctamente
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String rol = prefs.getString("user_rol", "CLIENTE");
        navegarSegunRol(rol);
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
            // Re-aplicar modo inmersivo cuando la ventana recupera el foco
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
