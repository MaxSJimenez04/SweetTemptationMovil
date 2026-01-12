package com.example.sweettemptation.dto;

public class ForgotPasswordRequest {
    private String correo;

    public ForgotPasswordRequest() {}

    public ForgotPasswordRequest(String correo) {
        this.correo = correo;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}

