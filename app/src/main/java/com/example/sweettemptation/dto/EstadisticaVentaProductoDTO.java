package com.example.sweettemptation.dto;

import java.util.Date;
public class EstadisticaVentaProductoDTO {
    Date fecha;
    int ventasPorDia;

    public EstadisticaVentaProductoDTO(Date fecha, int ventasPorDia) {
        this.fecha = fecha;
        this.ventasPorDia = ventasPorDia;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public int getVentasPorDia() {
        return ventasPorDia;
    }

    public void setVentasPorDia(int ventasPorDia) {
        this.ventasPorDia = ventasPorDia;
    }
}
