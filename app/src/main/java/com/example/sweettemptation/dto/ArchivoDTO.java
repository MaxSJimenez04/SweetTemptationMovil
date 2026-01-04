package com.example.sweettemptation.dto;

import java.io.Serializable;

public class ArchivoDTO implements Serializable {
    private int id;
    private String fechaRegistro;
    private String extension;
    private String datos;

    private int idProducto;

    public ArchivoDTO() {
    }


    public ArchivoDTO(int id, String fechaRegistro, String extension, String datos) {
        this.id = id;
        this.fechaRegistro = fechaRegistro;
        this.extension = extension;
        this.datos = datos;

    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getDatos() {
        return datos;
    }

    public void setDatos(String datos) {
        this.datos = datos;
    }
}
