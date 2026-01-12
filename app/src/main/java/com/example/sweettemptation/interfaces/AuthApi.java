package com.example.sweettemptation.interfaces;

import com.example.sweettemptation.dto.ForgotPasswordRequest;
import com.example.sweettemptation.dto.LoginRequest;
import com.example.sweettemptation.dto.LoginResponse;
import com.example.sweettemptation.dto.ResetPasswordRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApi {
    
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("auth/forgot-password")
    Call<ResponseBody> forgotPassword(@Body ForgotPasswordRequest request);

    @POST("auth/reset-password")
    Call<ResponseBody> resetPassword(@Body ResetPasswordRequest request);
}



