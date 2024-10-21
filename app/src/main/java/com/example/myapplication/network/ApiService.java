package com.example.myapplication.network;

import com.example.myapplication.model.ApiModels;
import com.example.myapplication.model.ApiModels.ApiResponse;
import com.example.myapplication.model.ApiModels.LoginRequest;
import com.example.myapplication.model.ApiModels.LoginResponse;
import com.example.myapplication.model.MyLedgerData;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import kotlin.ParameterName;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;


public interface ApiService {


    @POST("login/")
    Call<ApiResponse<LoginResponse>> login(@Body LoginRequest loginRequest);

    @POST("token/refresh/")
    Call<ApiResponse<LoginResponse>> refresh(@Body Map<String, String> refreshToken);

    @GET("token/check/")
    Call<ApiResponse<Objects>> checkToken();

    @POST("register/")
    Call<ApiResponse<Objects>> register(@Body LoginRequest loginRequest);

    @GET("user/")
    Call<ApiResponse<ApiModels.UserInfoResponse>> getUserInfo();

    @PUT("user/")
    Call<ApiResponse<Objects>> updateUserInfo(@Body Map<String, String> map);

    @Multipart
    @PUT("user/")
    Call<ApiResponse<Objects>> updateUserInfo(@Part MultipartBody.Part avatar);

    @POST("logout/")
    Call<ApiResponse<Objects>> logout(@Body Map<String, String> map);




    @GET("ledgers/")
    Call<ApiResponse<List<MyLedgerData>>> getLedgers();

    @GET("monthly-report/")
    Call<ApiResponse<ApiModels.MonthlyReportResponse>> getMonthlyReport(@QueryMap() Map<String, String> map);
}
