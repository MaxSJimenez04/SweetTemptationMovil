package com.example.sweettemptation.dto;

public class ResetPasswordRequest {
    private String token;
    private String nuevaContrasena;

    public ResetPasswordRequest() {}

    public ResetPasswordRequest(String token, String nuevaContrasena) {
        this.token = token;
        this.nuevaContrasena = nuevaContrasena;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNuevaContrasena() {
        return nuevaContrasena;
    }

    public void setNuevaContrasena(String nuevaContrasena) {
        this.nuevaContrasena = nuevaContrasena;
    }
}

