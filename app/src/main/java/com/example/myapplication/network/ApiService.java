package com.example.myapplication.network;

import com.example.myapplication.model.ApiModels.LoginRequest;
import com.example.myapplication.model.ApiModels.LoginResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;


public interface ApiService {


    @POST("login/")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);
}
