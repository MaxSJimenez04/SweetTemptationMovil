package com.example.sweettemptation.interfaces;

import com.example.sweettemptation.dto.LoginRequest;
import com.example.sweettemptation.dto.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApi {
    
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);
}

