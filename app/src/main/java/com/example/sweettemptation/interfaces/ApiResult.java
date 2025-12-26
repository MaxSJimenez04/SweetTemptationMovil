package com.example.sweettemptation.interfaces;

public class ApiResult<T> {
    private final boolean exito;
    public final T datos;
    public final int codigo;
    public final String mensaje;

    private ApiResult(boolean exito, T datos, int codigo, String mensaje) {
        this.exito = exito;
        this.datos = datos;
        this.codigo = codigo;
        this.mensaje = mensaje;
    }

    public static <T> ApiResult<T> exito(T datos, int codigo){
        return new ApiResult<>(true, datos, codigo, null);
    }

    public static <T> ApiResult<T> fallo(int codigo, String mensaje){
        return new ApiResult<>(false, null, codigo, mensaje);
    }
}
