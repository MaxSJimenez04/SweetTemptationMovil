package com.example.sweettemptation.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sweettemptation.MainActivity;
import android.content.SharedPreferences;
import com.example.sweettemptation.auth.TokenStorage;
import com.example.sweettemptation.databinding.ActivityLoginBinding;
import com.example.sweettemptation.dto.LoginRequest;
import com.example.sweettemptation.dto.LoginResponse;
import com.example.sweettemptation.interfaces.AuthApi;
import com.example.sweettemptation.network.ApiCliente;
import com.example.sweettemptation.utils.Constantes;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private TokenStorage tokenStorage;
    private AuthApi authApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Inicializar ViewBinding
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
    }

    private void configurarListeners() {
        // Botón de login
        binding.btnLogin.setOnClickListener(v -> intentarLogin());

        // Link de recuperar contraseña
        binding.tvRecuperar.setOnClickListener(v -> {
            // TODO: Implementar navegación a RecuperarContrasenaActivity
            Toast.makeText(this, "Función próximamente disponible", Toast.LENGTH_SHORT).show();
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
                    guardarDatosUsuario(loginResponse);
                    
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

    private void guardarDatosUsuario(LoginResponse response) {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        prefs.edit()
                .putInt("user_id", response.getId())
                .putString("user_nombre", response.getNombre())
                .putString("user_correo", response.getCorreo())
                .putString("user_telefono", response.getTelefono())
                .putString("user_rol", response.getRol())
                .apply();
    }

    private void navegarSegunRol(String rol) {
        Intent intent;
        
        if ("CLIENTE".equalsIgnoreCase(rol)) {
            intent = new Intent(this, ClienteActivity.class);
        } else {
            // Para ADMIN y EMPLEADO, por ahora ir a MainActivity
            // TODO: Crear AdminActivity y EmpleadoActivity
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}

