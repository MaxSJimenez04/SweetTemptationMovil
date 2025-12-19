package com.example.sweettemptation.dto;

import java.math.BigDecimal;

public class ProductoPedidoDTO {
    private int id;
    private BigDecimal subtotal;
    private int cantidad;
    private BigDecimal precioVenta;
    private int idPedido;
    private int idProducto;

    public ProductoPedidoDTO() {
    }

    public ProductoPedidoDTO(int id, BigDecimal subtotal, int cantidad, BigDecimal precioVenta, int idPedido, int idProducto) {
        this.id = id;
        this.subtotal = subtotal;
        this.cantidad = cantidad;
        this.precioVenta = precioVenta;
        this.idPedido = idPedido;
        this.idProducto = idProducto;
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

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
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
}
