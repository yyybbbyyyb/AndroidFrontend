package com.example.myapplication.network;

import android.content.Context;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class ApiClient {
     // 本地测试地址
//     private static final String BASE_URL = "http://10.0.2.2:8000/api/";
    // 服务器地址
    private static final String BASE_URL = "http://116.62.126.188/api/";

    private static Retrofit retrofit;

    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            // 日志拦截器
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Token拦截器
            AuthInterceptor authInterceptor = new AuthInterceptor(context);

            // Token刷新处理
            TokenAuthenticator tokenAuthenticator = new TokenAuthenticator(context);

            // OkHttpClient集成拦截器和认证器
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(authInterceptor)
                    .authenticator(tokenAuthenticator)  // 添加自动处理 token 刷新的机制
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
