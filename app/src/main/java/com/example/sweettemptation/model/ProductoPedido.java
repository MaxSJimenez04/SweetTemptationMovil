package com.example.sweettemptation.model;

import java.math.BigDecimal;

public class ProductoPedido {
    private int id;
    private BigDecimal subtotal;
    private int cantidad;
    private int idPedido;
    private int idProducto;
    private BigDecimal precioVenta;

    public ProductoPedido(int id, BigDecimal subtotal, int cantidad, int idPedido, int idProducto, BigDecimal precioVenta) {
        this.id = id;
        this.subtotal = subtotal;
        this.cantidad = cantidad;
        this.idPedido = idPedido;
        this.idProducto = idProducto;
        this.precioVenta = precioVenta;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
    }
}
