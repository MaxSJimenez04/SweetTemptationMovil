package com.example.sweettemptation.utils;

public class Constantes {
    public static final String PUERTO = "8080";
    public static final int PUERTO_GRPC = 9090;
    public static final String IP = "localhost";
    public static final String URL = "http://" + IP + ":" + PUERTO + "/";
    public static final String URL_GRPC= "http://" + IP + ":" + PUERTO;
    public static final int IVA = 16;
    public static final String MENSAJE_SIN_CONEXION= "No hay conexión con el servidor, intente de nuevo más tarde";
    public static final String MENSAJE_NO_AUTORIZADO = "No tienes permisos para realizar esta operación";
    public static final String MENSAJE_FALLA_SERVIDOR = "Ocurrió un error inesperado";
}
