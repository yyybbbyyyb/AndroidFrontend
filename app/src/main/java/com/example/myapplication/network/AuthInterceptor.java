package com.example.myapplication.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private Context context;

    public AuthInterceptor(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        SharedPreferences sharedPreferences = context.getSharedPreferences("my_shared_pref", Context.MODE_PRIVATE);
        String accessToken = sharedPreferences.getString("access_token", null);

        Request.Builder builder = chain.request().newBuilder();
        if (accessToken != null) {
            builder.addHeader("Authorization", "Bearer " + accessToken);
        }

        return chain.proceed(builder.build());
    }
}
