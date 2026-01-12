package com.example.sweettemptation.network;

import android.content.Context;

import com.example.sweettemptation.auth.AuthInterceptor;
import com.example.sweettemptation.auth.TokenStorage;
import com.example.sweettemptation.utils.Constantes;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public final class ApiCliente {

    private static volatile ApiCliente instance;
    private final Retrofit retrofit;

    private static final Interceptor UTF8_INTERCEPTOR = chain -> {
        Response response = chain.proceed(chain.request());
        if (response.body() != null && response.body().contentType() != null) {
            MediaType contentType = response.body().contentType();
            if (contentType.type().equals("application")) {
                MediaType utf8Type = MediaType.parse("application/json; charset=utf-8");
                ResponseBody body = ResponseBody.create(response.body().bytes(), utf8Type);
                return response.newBuilder().body(body).build();
            }
        }
        return response;
    };

    private ApiCliente(Context context) {
        Context appContext = context.getApplicationContext();
        TokenStorage tokenStorage = new TokenStorage(appContext);

        Moshi moshi = new Moshi.Builder()
                .add(new LocalDateTimeJsonAdapter())
                .add(new BigDecimalJsonAdapter()).add(new DateJsonAdapter())
                .build();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttp = new OkHttpClient.Builder()
                .callTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(UTF8_INTERCEPTOR)
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
            throw new IllegalStateException("ApiCliente no inicializado");
        }
        return instance;
    }

    public Retrofit retrofit() {
        return retrofit;
    }
}
