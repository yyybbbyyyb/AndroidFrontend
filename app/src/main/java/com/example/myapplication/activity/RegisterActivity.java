package com.example.myapplication.activity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.model.ApiModels;
import com.example.myapplication.network.ApiClient;
import com.example.myapplication.network.ApiService;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout tilUsername, tilPassword, tilConfirmPassword;
    private EditText etUsername, etPassword, etConfirmPassword;
    private Button btnRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 初始化控件
        tilUsername = findViewById(R.id.et_username);
        tilPassword = findViewById(R.id.et_password);
        tilConfirmPassword = findViewById(R.id.et_confirm_password);
        etUsername = tilUsername.getEditText();
        etPassword = tilPassword.getEditText();
        etConfirmPassword = tilConfirmPassword.getEditText();
        btnRegister = findViewById(R.id.btn_register);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String confirmPassword = etConfirmPassword.getText().toString().trim();

                if (!validateInputs(username, password, confirmPassword)) {
                    return;
                }

                register(username, password);
            }
        });

    }

    private void register(String username, String password) {
        ApiService apiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);
        Call<ApiModels.ApiResponse<Objects>> call = apiService.register(new ApiModels.LoginRequest(username, password));
        call.enqueue(new Callback<ApiModels.ApiResponse<Objects>>() {
            @Override
            public void onResponse(Call<ApiModels.ApiResponse<Objects>> call, Response<ApiModels.ApiResponse<Objects>> response) {
                if (response.isSuccessful()) {
                    ApiModels.ApiResponse<Objects> apiResponse = response.body();
                    if (apiResponse.getStatus().equals("success")) {
                        Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 1000); // 延迟1秒
                    } else {
                        // 注册失败
                        Toast.makeText(RegisterActivity.this, "注册失败: " + apiResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    try {
                        String error = response.errorBody().string();
                        Gson gson = new Gson();
                        ApiModels.ApiResponse<Objects> apiResponse = gson.fromJson(error, ApiModels.ApiResponse.class);
                        Map<String, List<String>> errors = (Map<String, List<String>>) apiResponse.getErrors();
                        List<String> usernameErrors = errors.get("username");

                        Toast.makeText(RegisterActivity.this, "注册失败: " + usernameErrors.get(0), Toast.LENGTH_LONG).show();

                    } catch (Exception e) {
                        Toast.makeText(RegisterActivity.this, "注册失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiModels.ApiResponse<Objects>> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "请求失败: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean validateInputs(String username, String password, String confirmPassword) {
        tilUsername.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);

        if (username.isEmpty()) {
            tilUsername.setError(getString(R.string.error_username_empty));
            return false;
        }

        if (username.length() < 3 || username.length() > 20) {
            tilUsername.setError(getString(R.string.error_username_length));
            return false;
        }

        if (password.isEmpty()) {
            tilPassword.setError(getString(R.string.error_password_empty));
            return false;
        }

        if (password.length() < 6 || password.length() > 20) {
            tilPassword.setError(getString(R.string.error_password_length));
            return false;
        }

        if (confirmPassword.isEmpty()) {
            tilConfirmPassword.setError(getString(R.string.error_password_confirm_empty));
            return false;
        }

        if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError(getString(R.string.error_password_confirm));
            return false;
        }

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        etUsername.setText("");
        etPassword.setText("");
        etConfirmPassword.setText("");
    }


}
