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
        
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        configurarBarrasSistema();

        ApiCliente.init(this);
        tokenStorage = new TokenStorage(this);
        authApi = ApiCliente.getInstance().retrofit().create(AuthApi.class);

        if (tokenStorage.getToken() != null) {
            navegarAMain();
            return;
        }

        configurarListeners();
        configurarOnBackPressed();
    }

    private void configurarListeners() {
        binding.btnLogin.setOnClickListener(v -> intentarLogin());
        binding.tvRecuperar.setOnClickListener(v -> mostrarRecuperarPassword());
    }

    private void mostrarRecuperarPassword() {
        fragmentRecuperarVisible = true;
        binding.loginContainer.setVisibility(View.GONE);
        binding.fragmentContainer.setVisibility(View.VISIBLE);
        
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
        
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .remove(fragment)
                    .commit();
        }
        
        binding.fragmentContainer.setVisibility(View.GONE);
        binding.loginContainer.setVisibility(View.VISIBLE);
    }

    private void configurarOnBackPressed() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (fragmentRecuperarVisible) {
                    ocultarRecuperarPassword();
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }

    private void intentarLogin() {
        CharSequence usuarioText = binding.etUsuario.getText();
        CharSequence contrasenaText = binding.etPassword.getText();
        String usuario = usuarioText != null ? usuarioText.toString().trim() : "";
        String contrasena = contrasenaText != null ? contrasenaText.toString().trim() : "";

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

        mostrarLoading(true);

        LoginRequest request = new LoginRequest(usuario, contrasena);
        
        authApi.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                mostrarLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    tokenStorage.saveToken(loginResponse.getToken());
                    UserSession.save(LoginActivity.this, loginResponse);
                    navegarSegunRol(loginResponse.getRol());
                } else if (response.code() == 401 || response.code() == 403) {
                    Toast.makeText(LoginActivity.this, "Credenciales incorrectas", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginActivity.this, Constantes.MENSAJE_FALLA_SERVIDOR, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                mostrarLoading(false);
                Toast.makeText(LoginActivity.this, Constantes.MENSAJE_SIN_CONEXION, Toast.LENGTH_LONG).show();
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
            intent = new Intent(this, AdminActivity.class);
        } else if ("CLIENTE".equalsIgnoreCase(rol)) {
            intent = new Intent(this, ClienteActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }
        
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void navegarAMain() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String rol = prefs.getString("user_rol", "CLIENTE");
        navegarSegunRol(rol);
    }

    private void configurarBarrasSistema() {
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
        
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
