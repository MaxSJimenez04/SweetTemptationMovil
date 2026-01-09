package com.example.sweettemptation.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.sweettemptation.dto.LoginResponse;

public final class UserSession {

    private static final String PREFS_NAME = "user_prefs";

    private static final String KEY_ID = "user_id";
    private static final String KEY_NOMBRE = "user_nombre";
    private static final String KEY_CORREO = "user_correo";
    private static final String KEY_TELEFONO = "user_telefono";
    private static final String KEY_ROL = "user_rol";

    private UserSession() {
    }
    public static void save(Context context, LoginResponse response) {
        SharedPreferences prefs =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        prefs.edit()
                .putInt(KEY_ID, response.getId())
                .putString(KEY_NOMBRE, response.getNombre())
                .putString(KEY_CORREO, response.getCorreo())
                .putString(KEY_TELEFONO, response.getTelefono())
                .putString(KEY_ROL, response.getRol())
                .apply();
    }

    public static int getUserId(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getInt(KEY_ID, -1);
    }

    public static String getNombre(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString(KEY_NOMBRE, null);
    }

    public static String getCorreo(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString(KEY_CORREO, null);
    }

    public static String getTelefono(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString(KEY_TELEFONO, null);
    }

    public static String getRol(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString(KEY_ROL, null);
    }


    public static boolean isLogged(Context context) {
        return getUserId(context) != -1;
    }

    public static void clear(Context context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply();
    }
}
