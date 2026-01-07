package com.example.sweettemptation.dto;

public class PagoResponse {
    private int idPago;
    private String mensajeConfirmacion;
    private double cambioDevuelto;
    private double totalPagado;

    public String getMensajeConfirmacion() { return mensajeConfirmacion; }
    public double getCambioDevuelto() { return cambioDevuelto; }
}