package com.example.sweettemptation.interfaces;

import com.example.sweettemptation.dto.EstadisticaProductoDTO;
import com.example.sweettemptation.dto.EstadisticaVentaProductoDTO;
import com.example.sweettemptation.dto.PedidoDTO;

import java.time.LocalDate;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

public interface EstadisticasApi {

    //Estadisticas Ventas
    @GET("estadisticas/ventas")
    Call<List<PedidoDTO>> consultarVentas(
            @Query("fechaInicio") String fechaInicio,
            @Query("fechaFin") String fechaFin,
            @Query("estado") String estadoTexto
    );

    @Streaming
    @GET("estadisticas/ventas/descargarCSV")
    Call<ResponseBody> descargarReporte(
            @Query("fechaInicio") String fechaInicio,
            @Query("fechaFin") String fechaFin,
            @Query("estado") String estado
    );

    //Estadisticas Producto
    @GET("estadisticas/productos/")
    Call<List<EstadisticaProductoDTO>> obtenerEstadisticasProductos(@Query("fechaInicio")LocalDate fechaInicio,
                                                                    @Query("fechaFin") LocalDate fechaFin);
    @GET("estadisticas/productos/{id}")
    Call<List<EstadisticaVentaProductoDTO>> obtenerVentasProducto(@Path("id") int idProducto,
                                                                  @Query("fechaInicio") LocalDate fechaInicio,
                                                                  @Query("fechaFin") LocalDate fechaFin);

}
