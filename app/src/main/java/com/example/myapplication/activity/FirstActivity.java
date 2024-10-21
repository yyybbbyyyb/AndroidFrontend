package com.example.myapplication.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

import com.example.myapplication.network.ApiClient;
import com.example.myapplication.network.ApiService;
import com.example.myapplication.model.ApiModels;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FirstActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        // 立即显示布局，避免白屏

        // 3 秒后执行跳转逻辑
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getSharedPreferences("my_shared_pref", MODE_PRIVATE);
                String accessToken = sharedPreferences.getString("access_token", null);

                if (accessToken != null) {
                    checkTokenValidity(accessToken);
                } else {
                    redirectToLogin();
                }
            }
        }, 1000); // 延迟3秒
    }

    private void checkTokenValidity(String accessToken) {

        ApiService apiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);
        Call<ApiModels.ApiResponse<Objects>> call = apiService.checkToken();

        call.enqueue(new Callback<ApiModels.ApiResponse<Objects>>() {
            @Override
            public void onResponse(Call<ApiModels.ApiResponse<Objects>> call, Response<ApiModels.ApiResponse<Objects>> response) {
                if (response.isSuccessful()) {
                    startMainActivity();
                } else {
                    // 请求失败，跳转到登录界面
                    redirectToLogin();
                }
            }

            @Override
            public void onFailure(Call<ApiModels.ApiResponse<Objects>> call, Throwable t) {
                // 请求失败，跳转到登录界面
                redirectToLogin();
            }
        });
    }

    private void redirectToLogin() {
        Intent intent = new Intent(FirstActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void startMainActivity() {
        Intent intent = new Intent(FirstActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
