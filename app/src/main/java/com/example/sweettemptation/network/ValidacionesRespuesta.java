package com.example.sweettemptation.network;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import okhttp3.ResponseBody;

public class ValidacionesRespuesta {
    public static boolean esDesconexion(Throwable t) {
        return (t instanceof UnknownHostException)
                || (t instanceof ConnectException)
                || (t instanceof SocketTimeoutException)
                || (t instanceof IOException);
    }

    public static String leerErrorBody(ResponseBody errorBody) {
        if (errorBody == null) return null;
        try {
            return errorBody.string();
        } catch (Exception e) {
            return null;
        }
    }
}
