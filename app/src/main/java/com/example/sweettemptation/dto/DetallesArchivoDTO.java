package com.example.sweettemptation.dto;

import java.time.LocalDateTime;

public class DetallesArchivoDTO {
    private int id;
    private LocalDateTime fechaRegistro;
    private String extension;
    private String ruta;

    public DetallesArchivoDTO() {
    }

    public DetallesArchivoDTO(int id, LocalDateTime fechaRegistro, String extension, String ruta) {
        this.id = id;
        this.fechaRegistro = fechaRegistro;
        this.extension = extension;
        this.ruta = ruta;
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

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }
}
