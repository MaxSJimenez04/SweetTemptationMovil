package com.example.sweettemptation.dto;

import java.io.Serializable;

public class ProductoRequest implements Serializable {
    private ProductoDTO producto;
    private ArchivoDTO archivo;

    public ProductoRequest(ProductoDTO producto, ArchivoDTO archivo) {
        this.producto = producto;
        this.archivo = archivo;
    }

    // Getters y Setters
    public ProductoDTO getProducto() { return producto; }
    public void setProducto(ProductoDTO producto) { this.producto = producto; }
    public ArchivoDTO getArchivo() { return archivo; }
    public void setArchivo(ArchivoDTO archivo) { this.archivo = archivo; }
}