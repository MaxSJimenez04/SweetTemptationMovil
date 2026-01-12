package com.example.sweettemptation.dto;

import com.google.gson.annotations.SerializedName;

public class SolicitudPersonalizadaDTO {
    @SerializedName("idCliente")
    private int idCliente;
    private String tamano;
    private String saborBizcocho;
    private String relleno;
    private String cobertura;
    private String especificaciones;
    private String imagenUrl;
    private String telefonoContacto;

    public SolicitudPersonalizadaDTO(int idCliente, String tamano, String saborBizcocho, String relleno, String cobertura, String especificaciones, String imagenUrl, String telefonoContacto) {
        this.idCliente = idCliente;
        this.tamano = tamano;
        this.saborBizcocho = saborBizcocho;
        this.relleno = relleno;
        this.cobertura = cobertura;
        this.especificaciones = especificaciones;
        this.imagenUrl = imagenUrl;
        this.telefonoContacto = telefonoContacto;
    }


    public int getIdCliente() { return idCliente; }
    public String getTamano() { return tamano; }
    public String getSaborBizcocho() { return saborBizcocho; }
    public String getRelleno() { return relleno; }
    public String getCobertura() { return cobertura; }
    public String getEspecificaciones() { return especificaciones; }
    public String getImagenUrl() { return imagenUrl; }
    public String getTelefonoContacto() { return telefonoContacto; }

    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
    public void setTamano(String tamano) { this.tamano = tamano; }
    public void setSaborBizcocho(String saborBizcocho) { this.saborBizcocho = saborBizcocho; }
    public void setRelleno(String relleno) { this.relleno = relleno; }
    public void setCobertura(String cobertura) { this.cobertura = cobertura; }
    public void setEspecificaciones(String especificaciones) { this.especificaciones = especificaciones; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    public void setTelefonoContacto(String telefonoContacto) { this.telefonoContacto = telefonoContacto; }
}