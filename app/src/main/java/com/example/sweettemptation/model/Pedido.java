package com.example.sweettemptation.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Pedido {
    private int id;
    private LocalDateTime fechaCompra;
    private Boolean actual;
    private BigDecimal total;
    private int estado;
    private Boolean personalizado;
    private int idCliente;

    public Pedido() {
    }

    public Pedido(int id, LocalDateTime fechaCompra, Boolean actual, BigDecimal total, int estado, Boolean personalizado, int idCliente) {
        this.id = id;
        this.fechaCompra = fechaCompra;
        this.actual = actual;
        this.total = total;
        this.estado = estado;
        this.personalizado = personalizado;
        this.idCliente = idCliente;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(LocalDateTime fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public Boolean getActual() {
        return actual;
    }

    public void setActual(Boolean actual) {
        this.actual = actual;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public Boolean getPersonalizado() {
        return personalizado;
    }

    public void setPersonalizado(Boolean personalizado) {
        this.personalizado = personalizado;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }
}
