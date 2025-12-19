package com.example.sweettemptation.dto;

import java.time.LocalDateTime;

public class ArchivoDTO {
    private int id;
    private LocalDateTime fechaRegistro;
    private String extension;
    private byte[] datos;

    public ArchivoDTO() {
    }

    public ArchivoDTO(int id, LocalDateTime fechaRegistro, String extension, byte[] datos) {
        this.id = id;
        this.fechaRegistro = fechaRegistro;
        this.extension = extension;
        this.datos = datos;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public byte[] getDatos() {
        return datos;
    }

    public void setDatos(byte[] datos) {
        this.datos = datos;
    }
}
