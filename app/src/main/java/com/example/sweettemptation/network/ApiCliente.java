package com.example.sweettemptation.network;

import android.content.Context;

import com.example.sweettemptation.utils.Constantes;
import com.squareup.moshi.Moshi;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;
import com.example.sweettemptation.auth.TokenStorage;
import com.example.sweettemptation.auth.AuthInterceptor;

public final class ApiCliente {

    private static volatile ApiCliente instance;

    private final Retrofit retrofit;

    private ApiCliente(Context context) {
        Context appContext = context.getApplicationContext();

        TokenStorage tokenStorage = new TokenStorage(appContext);

        Moshi moshi = new Moshi.Builder().build();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttp = new OkHttpClient.Builder()
                // Timeouts para desarrollo
                .callTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)

                .addInterceptor(new AuthInterceptor(tokenStorage))
                .addInterceptor(logging)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(Constantes.URL)
                .client(okHttp)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build();
    }

    public static void init(Context context) {
        if (instance == null) {
            synchronized (ApiCliente.class) {
                if (instance == null) {
                    instance = new ApiCliente(context);
                }
            }
        }
    }

    public static ApiCliente getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ApiCliente no inicializado. Llama ApiCliente.init(context) en Application/Activity.");
        }
        return instance;
    }

    // Si quieres exponer Retrofit:
    public Retrofit retrofit() {
        return retrofit;
    }
}
