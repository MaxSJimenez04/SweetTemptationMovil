package com.example.sweettemptation.interfaces;

import com.example.sweettemptation.dto.EstadisticaProductoDTO;
import com.example.sweettemptation.dto.EstadisticaVentaProductoDTO;

import java.time.LocalDate;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface EstadisticasApi {

    //Estadisticas Ventas

    //Estadisticas Producto
    @GET("estadisticas/productos/")
    Call<List<EstadisticaProductoDTO>> obtenerEstadisticasProductos(@Query("fechaInicio")LocalDate fechaInicio,
                                                                    @Query("fechaFin") LocalDate fechaFin);
    @GET("estadisticas/productos/{id}")
    Call<List<EstadisticaVentaProductoDTO>> obtenerVentasProducto(@Path("id") int idProducto,
                                                                  @Query("fechaInicio") LocalDate fechaInicio,
                                                                  @Query("fechaFin") LocalDate fechaFin);

}
