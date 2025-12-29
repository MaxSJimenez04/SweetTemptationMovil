package com.example.sweettemptation.dto;

public class EstadisticaProductoDTO {
    private String categoria;
    private String nombre;
    private int ventas;
    public EstadisticaProductoDTO(String categoria, String nombre, int ventas) {
        this.categoria = categoria;
        this.nombre = nombre;
        this.ventas = ventas;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getVentas() {
        return ventas;
    }

    public void setVentas(int ventas) {
        this.ventas = ventas;
    }
}
