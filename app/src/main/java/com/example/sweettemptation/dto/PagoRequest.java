package com.example.sweettemptation.dto;

public class PagoRequest {
    private String tipoPago;
    private double montoPagado;
    private String detallesCuenta;

    public PagoRequest(String tipoPago, double montoPagado, String detallesCuenta) {
        this.tipoPago = tipoPago;
        this.montoPagado = montoPagado;
        this.detallesCuenta = detallesCuenta;
    }
}