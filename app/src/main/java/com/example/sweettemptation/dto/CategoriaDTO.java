package com.example.sweettemptation.dto;

import androidx.annotation.NonNull;

public class CategoriaDTO {

    private Integer id;
    private String nombre;

    // Constructor vacío (Necesario para Retrofit/JSON)
    public CategoriaDTO() {
    }

    // Constructor con parámetros
    public CategoriaDTO(Integer id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * IMPORTANTE: El Spinner de Android usa este método para mostrar el texto.
     * Si devolvemos solo 'nombre', el usuario verá una lista limpia.
     */
    @NonNull
    @Override
    public String toString() {
        return nombre != null ? nombre : "";
    }
}