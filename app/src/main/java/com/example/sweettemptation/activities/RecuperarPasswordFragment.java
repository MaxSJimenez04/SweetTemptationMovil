package com.example.sweettemptation.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sweettemptation.databinding.FragmentRecuperarPasswordBinding;
import com.example.sweettemptation.dto.ForgotPasswordRequest;
import com.example.sweettemptation.dto.ResetPasswordRequest;
import com.example.sweettemptation.interfaces.AuthApi;
import com.example.sweettemptation.network.ApiCliente;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecuperarPasswordFragment extends Fragment {

    private FragmentRecuperarPasswordBinding binding;
    private AuthApi authApi;
    private String correoGuardado = "";
    
    // Estados del fragment
    private static final int PASO_CORREO = 1;
    private static final int PASO_CAMBIO = 2;
    private int pasoActual = PASO_CORREO;

    // Interfaz para comunicar con LoginActivity
    public interface OnRecuperarPasswordListener {
        void onCerrarRecuperarPassword();
    }

    private OnRecuperarPasswordListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnRecuperarPasswordListener) {
            listener = (OnRecuperarPasswordListener) context;
        } else {
            throw new RuntimeException(context.toString() + " debe implementar OnRecuperarPasswordListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRecuperarPasswordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ApiCliente.init(requireContext());
        authApi = ApiCliente.getInstance().retrofit().create(AuthApi.class);

        configurarListeners();
        mostrarPaso(PASO_CORREO);
    }

    private void configurarListeners() {
        // Botón cerrar
        binding.btnCerrar.setOnClickListener(v -> cerrarFragment());

        // Botón enviar código (Paso 1)
        binding.btnEnviarCodigo.setOnClickListener(v -> enviarCodigo());

        // Botón cambiar contraseña (Paso 2)
        binding.btnCambiarContrasena.setOnClickListener(v -> cambiarContrasena());

        // Link reenviar código
        binding.tvReenviarCodigo.setOnClickListener(v -> {
            mostrarPaso(PASO_CORREO);
            binding.etCorreo.setText(correoGuardado);
        });
    }

    private void mostrarPaso(int paso) {
        pasoActual = paso;
        
        if (paso == PASO_CORREO) {
            binding.layoutPasoCorreo.setVisibility(View.VISIBLE);
            binding.layoutPasoCambio.setVisibility(View.GONE);
            binding.tvTitulo.setText("Recuperar Contraseña");
            binding.tvSubtitulo.setText("Ingresa tu correo electrónico para recibir un código de verificación");
        } else {
            binding.layoutPasoCorreo.setVisibility(View.GONE);
            binding.layoutPasoCambio.setVisibility(View.VISIBLE);
            binding.tvTitulo.setText("Cambiar Contraseña");
            binding.tvSubtitulo.setText("Ingresa el código que recibiste en tu correo y tu nueva contraseña");
        }
    }

    private void enviarCodigo() {
        String correo = binding.etCorreo.getText().toString().trim();

        // Validar correo
        if (correo.isEmpty()) {
            binding.etCorreo.setError("Ingresa tu correo");
            binding.etCorreo.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            binding.etCorreo.setError("Ingresa un correo válido");
            binding.etCorreo.requestFocus();
            return;
        }

        mostrarLoading(true);
        correoGuardado = correo;

        ForgotPasswordRequest request = new ForgotPasswordRequest(correo);
        authApi.forgotPassword(request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mostrarLoading(false);

                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), 
                            "Se envió un código a tu correo", 
                            Toast.LENGTH_LONG).show();
                    mostrarPaso(PASO_CAMBIO);
                } else if (response.code() == 404) {
                    Toast.makeText(requireContext(), 
                            "No existe una cuenta con ese correo", 
                            Toast.LENGTH_LONG).show();
                } else {
                    String errorMsg = obtenerMensajeError(response);
                    Toast.makeText(requireContext(), 
                            errorMsg != null ? errorMsg : "Error al enviar el código", 
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mostrarLoading(false);
                Toast.makeText(requireContext(), 
                        "Error de conexión. Verifica tu internet.", 
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void cambiarContrasena() {
        String token = binding.etToken.getText().toString().trim();
        String nuevaContrasena = binding.etNuevaContrasena.getText().toString().trim();
        String confirmarContrasena = binding.etConfirmarContrasena.getText().toString().trim();

        // Validaciones
        if (token.isEmpty()) {
            binding.etToken.setError("Ingresa el código");
            binding.etToken.requestFocus();
            return;
        }

        if (nuevaContrasena.isEmpty()) {
            binding.etNuevaContrasena.setError("Ingresa tu nueva contraseña");
            binding.etNuevaContrasena.requestFocus();
            return;
        }

        if (nuevaContrasena.length() < 6) {
            binding.etNuevaContrasena.setError("La contraseña debe tener al menos 6 caracteres");
            binding.etNuevaContrasena.requestFocus();
            return;
        }

        if (confirmarContrasena.isEmpty()) {
            binding.etConfirmarContrasena.setError("Confirma tu contraseña");
            binding.etConfirmarContrasena.requestFocus();
            return;
        }

        if (!nuevaContrasena.equals(confirmarContrasena)) {
            binding.etConfirmarContrasena.setError("Las contraseñas no coinciden");
            binding.etConfirmarContrasena.requestFocus();
            return;
        }

        mostrarLoading(true);

        ResetPasswordRequest request = new ResetPasswordRequest(token, nuevaContrasena);
        authApi.resetPassword(request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mostrarLoading(false);

                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), 
                            "¡Contraseña cambiada exitosamente!", 
                            Toast.LENGTH_LONG).show();
                    cerrarFragment();
                } else {
                    String errorMsg = obtenerMensajeError(response);
                    if (errorMsg != null) {
                        Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show();
                    } else if (response.code() == 404) {
                        Toast.makeText(requireContext(), 
                                "Código inválido o expirado", 
                                Toast.LENGTH_LONG).show();
                    } else if (response.code() == 400) {
                        Toast.makeText(requireContext(), 
                                "No puedes usar una contraseña anterior", 
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(requireContext(), 
                                "Error al cambiar la contraseña", 
                                Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mostrarLoading(false);
                Toast.makeText(requireContext(), 
                        "Error de conexión. Verifica tu internet.", 
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private String obtenerMensajeError(Response<?> response) {
        try {
            if (response.errorBody() != null) {
                return response.errorBody().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void mostrarLoading(boolean mostrar) {
        binding.progressBar.setVisibility(mostrar ? View.VISIBLE : View.GONE);
        
        if (pasoActual == PASO_CORREO) {
            binding.btnEnviarCodigo.setEnabled(!mostrar);
            binding.etCorreo.setEnabled(!mostrar);
        } else {
            binding.btnCambiarContrasena.setEnabled(!mostrar);
            binding.etToken.setEnabled(!mostrar);
            binding.etNuevaContrasena.setEnabled(!mostrar);
            binding.etConfirmarContrasena.setEnabled(!mostrar);
        }
    }

    private void cerrarFragment() {
        if (listener != null) {
            listener.onCerrarRecuperarPassword();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}

