package com.example.sweettemptation.dto;

import java.math.BigDecimal;

public class DetallesProductoDTO {
    private int id;
    private int cantidad;
    private String nombre;
    private BigDecimal precio;
    private BigDecimal subtotal;
    int idProducto;

    public DetallesProductoDTO() {
    }

    public DetallesProductoDTO(int id, int cantidad, String nombre, BigDecimal precio, BigDecimal subtotal, int idProducto) {
        this.id = id;
        this.cantidad = cantidad;
        this.nombre = nombre;
        this.precio = precio;
        this.subtotal = subtotal;
        this.idProducto = idProducto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }
}
