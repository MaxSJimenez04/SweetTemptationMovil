package com.example.sweettemptation.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
public class ProductoDTO implements Serializable{
    private static final long serialVersionUID = 1L;
    private int id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private boolean disponible;
    private int unidades;
    private String fechaRegistro;
    private String fechaModificacion;
    private int categoria;

    public ProductoDTO() {
    }

    public ProductoDTO(String nombre, String descripcion, BigDecimal precio,
                       boolean disponible, int unidades, int categoria) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.disponible = disponible;
        this.unidades = unidades;
        this.categoria = categoria;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    public int getUnidades() { return unidades; }
    public void setUnidades(int unidades) { this.unidades = unidades; }

    public String getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(String fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public String getFechaModificacion() { return fechaModificacion; }
    public void setFechaModificacion(String fechaModificacion) { this.fechaModificacion = fechaModificacion; }

    public int getCategoria() { return categoria; }
    public void setCategoria(int categoria) { this.categoria = categoria; }
}