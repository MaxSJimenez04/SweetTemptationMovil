package com.example.sweettemptation.auth;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenStorage {

    private static final String PREFS = "auth_prefs";
    private static final String KEY_TOKEN = "jwt_token";

    private final SharedPreferences sp;

    public TokenStorage(Context context) {
        sp = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void saveToken(String token) {
        sp.edit().putString(KEY_TOKEN, token).apply();
    }

    public String getToken() {
        return sp.getString(KEY_TOKEN, null);
    }

    public void clear() {
        sp.edit().remove(KEY_TOKEN).apply();
    }
}
