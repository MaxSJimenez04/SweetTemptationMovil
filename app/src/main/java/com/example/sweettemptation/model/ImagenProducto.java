package com.example.sweettemptation.model;

import java.time.LocalDateTime;

public class ImagenProducto {
    private int id;
    private int idProducto;
    private int idArchivo;
    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaAsociacion;

    public ImagenProducto(int id, int idProducto, int idArchivo, LocalDateTime fechaRegistro, LocalDateTime fechaAsociacion) {
        this.id = id;
        this.idProducto = idProducto;
        this.idArchivo = idArchivo;
        this.fechaRegistro = fechaRegistro;
        this.fechaAsociacion = fechaAsociacion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getIdArchivo() {
        return idArchivo;
    }

    public void setIdArchivo(int idArchivo) {
        this.idArchivo = idArchivo;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public LocalDateTime getFechaAsociacion() {
        return fechaAsociacion;
    }

    public void setFechaAsociacion(LocalDateTime fechaAsociacion) {
        this.fechaAsociacion = fechaAsociacion;
    }
}
