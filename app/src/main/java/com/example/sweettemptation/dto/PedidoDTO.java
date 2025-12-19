package com.example.sweettemptation.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PedidoDTO {
    private int id;
    private LocalDateTime fechaCompra;
    private Boolean actual;
    private BigDecimal total;
    private int estado;
    private Boolean personalizado;
    private int idCliente;

    String nombreRol;
    int idRol;

    public PedidoDTO() {
    }

    public PedidoDTO(int id, LocalDateTime fechaCompra, Boolean actual, BigDecimal total, int estado, Boolean personalizado, int idCliente) {
        this.id = id;
        this.fechaCompra = fechaCompra;
        this.actual = actual;
        this.total = total;
        this.estado = estado;
        this.personalizado = personalizado;
        this.idCliente = idCliente;
    }

    // Constructor para la consulta de ventas
    public PedidoDTO(int id, LocalDateTime fechaCompra, Boolean actual, BigDecimal total,int estado, Boolean personalizado, int idCliente, int idRol) {
        this(id, fechaCompra, actual, total, estado, personalizado, idCliente);
        this.idRol = idRol;
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

    public String getNombreRol() {
        return nombreRol;
    }

    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
    }

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }
}
