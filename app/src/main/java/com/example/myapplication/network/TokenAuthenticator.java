package com.example.myapplication.network;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.myapplication.activity.LoginActivity;
import com.example.myapplication.model.ApiModels;

import java.io.IOException;
import java.util.Map;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Call;

public class TokenAuthenticator implements Authenticator {

    private final Context context;

    public TokenAuthenticator(Context context) {
        this.context = context;
    }

    @Override
    public Request authenticate(Route route, @NonNull Response response) throws IOException {
        if (response.request().url().toString().contains("/token/refresh")) {
            Log.d("TokenAuthenticator", "Token refresh failed, stopping.");
            return null;
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences("my_shared_pref", Context.MODE_PRIVATE);
        String refreshToken = sharedPreferences.getString("refresh_token", null);

        if (refreshToken == null) {
            return null;
        }

        ApiService apiService = ApiClient.getClient(context).create(ApiService.class);
        Map<String, String> refreshTokenMap = new java.util.HashMap<>();
        refreshTokenMap.put("refresh", refreshToken);
        Call<ApiModels.ApiResponse<ApiModels.LoginResponse>> call = apiService.refresh(refreshTokenMap);
        retrofit2.Response<ApiModels.ApiResponse<ApiModels.LoginResponse>> tokenResponse = call.execute();

        if (tokenResponse.isSuccessful()) {
            ApiModels.LoginResponse loginResponse = tokenResponse.body().getData();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("access_token", loginResponse.getAccess());
            editor.apply();

            Log.d("Token", "Token refreshed successfully");

            return response.request().newBuilder()
                    .header("Authorization", "Bearer " + loginResponse.getAccess())
                    .build();
        } else {
            // 刷新 token 失败
            Log.d("Token", "Token refresh failed");

            // 在主线程中显示 Toast，并跳转到登录界面
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "登录过期，请重新登录", Toast.LENGTH_LONG).show();

                    // 清除 SharedPreferences 中的 token
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("access_token");
                    editor.remove("refresh_token");
                    editor.apply();

                    // 跳转到登录界面
                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);
                }
            });
            return null;
        }
    }

}
