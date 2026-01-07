package com.example.sweettemptation.model;

import java.math.BigDecimal;

public class Pago {
    private int id;
    private BigDecimal total;
    private String fechaPago;
    private String tipoPago;
    private String cuenta;
    private int idPedido;

    public Pago() {}

    public Pago(int id, BigDecimal total, String fechaPago, String tipoPago, String cuenta, int idPedido) {
        this.id = id;
        this.total = total;
        this.fechaPago = fechaPago;
        this.tipoPago = tipoPago;
        this.cuenta = cuenta;
        this.idPedido = idPedido;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public String getFechaPago() { return fechaPago; }
    public void setFechaPago(String fechaPago) { this.fechaPago = fechaPago; }

    public String getTipoPago() { return tipoPago; }
    public void setTipoPago(String tipoPago) { this.tipoPago = tipoPago; }

    public String getCuenta() { return cuenta; }
    public void setCuenta(String cuenta) { this.cuenta = cuenta; }

    public int getIdPedido() { return idPedido; }

    public void setIdPedido(int idPedido) { this.idPedido = idPedido; }
}