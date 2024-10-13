package com.example.myapplication.activity;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.network.ApiClient;
import com.example.myapplication.network.ApiService;
import com.example.myapplication.model.ApiModels.LoginRequest;
import com.example.myapplication.model.ApiModels.LoginResponse;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 创建 Retrofit API 服务
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        // 创建登录请求体
        LoginRequest loginRequest = new LoginRequest("1", "111");

        // 发起登录请求
        Call<LoginResponse> call = apiService.login(loginRequest);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    // 请求成功，处理登录响应
                    LoginResponse loginResponse = response.body();
                    Log.d("Login", "Access Token: " + loginResponse.getAccess());
                } else {
                    // 请求失败，解析错误响应
                    String errorBody = null;
                    try {
                        errorBody = response.errorBody().string();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Gson gson = new Gson();

                    // 将错误响应解析为 JsonObject
                    JsonObject errorJson = gson.fromJson(errorBody, JsonObject.class);

                    // 获取通用的错误消息
                    if (errorJson.has("message")) {
                        String message = errorJson.get("message").getAsString();
                        Log.e("Login", "Error message: " + message);
                    }

                    // 检查是否包含错误详细信息
                    if (errorJson.has("errors")) {
                        JsonObject errors = errorJson.getAsJsonObject("errors");
                        for (Map.Entry<String, JsonElement> entry : errors.entrySet()) {
                            String field = entry.getKey();  // 错误字段名
                            JsonElement errorMessages = entry.getValue();  // 错误消息数组

                            Log.e("Login", "Error in " + field + ": " + errorMessages.toString());
                        }
                    }
                }
            }


            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // 请求失败
                Log.e("Login", "Request failed: " + t.getMessage());
            }
        });
    }
}
