package com.example.myapplication.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.model.ApiModels;
import com.example.myapplication.network.ApiClient;
import com.example.myapplication.network.ApiService;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.myapplication.utils.SnackbarUtils;
import com.example.myapplication.utils.Utils;

public class LoginActivity extends AppCompatActivity {

    // 初始化控件
    private TextInputLayout tilUsername, tilPassword;
    private EditText etUsername, etPassword;
    private Button btnLogin, btnRegister;
    private CheckBox checkboxAgreement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 初始化控件
        tilUsername = findViewById(R.id.et_username);
        tilPassword = findViewById(R.id.et_password);
        etUsername = tilUsername.getEditText();
        etPassword = tilPassword.getEditText();
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
        checkboxAgreement = findViewById(R.id.checkbox_agreement);

        setupClickableAgreement();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.hideKeyboard(view, LoginActivity.this);

                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (!validateInputs(username, password)) {
                    return;
                }

                if (!checkboxAgreement.isChecked()) {
                    Toast.makeText(LoginActivity.this, "请先同意用户协议和隐私策略", Toast.LENGTH_LONG).show();
                    return;
                }

                loginUser(username, password);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 清除输入框的内容
        etUsername.setText("");
        etPassword.setText("");

        // 清除错误提示
        tilUsername.setError(null);
        tilPassword.setError(null);

        // 取消勾选复选框
        checkboxAgreement.setChecked(false);
    }

    private void loginUser(String username, String password) {
        ApiService apiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);
        ApiModels.LoginRequest loginRequest = new ApiModels.LoginRequest(username, password);

        Call<ApiModels.ApiResponse<ApiModels.LoginResponse>> call = apiService.login(loginRequest);
        call.enqueue(new Callback<ApiModels.ApiResponse<ApiModels.LoginResponse>>() {
            @Override
            public void onResponse(Call<ApiModels.ApiResponse<ApiModels.LoginResponse>> call, Response<ApiModels.ApiResponse<ApiModels.LoginResponse>> response) {
                if (response.isSuccessful()) {
                    ApiModels.ApiResponse<ApiModels.LoginResponse> apiResponse = response.body();
                    if (apiResponse != null && apiResponse.getData() != null) {
                        ApiModels.LoginResponse loginResponse = apiResponse.getData();
                        saveTokens(loginResponse.getAccess(), loginResponse.getRefresh());

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    try {
                        SnackbarUtils.showCustomSnackbar(findViewById(android.R.id.content),
                                "用户名或密码错误",
                                3000,
                                Color.WHITE,
                                Color.RED,
                                16,
                                false,
                                false,
                                true);
                    } catch (Exception e) {
                        Toast.makeText(LoginActivity.this, "登录失败，未知错误", Toast.LENGTH_LONG).show();
                    }
                    tilUsername.getEditText().setText("");
                    tilPassword.getEditText().setText("");
                }
            }

            @Override
            public void onFailure(Call<ApiModels.ApiResponse<ApiModels.LoginResponse>> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "请求失败: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveTokens(String accessToken, String refreshToken) {
        SharedPreferences sharedPreferences = getSharedPreferences("my_shared_pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("access_token", accessToken);
        editor.putString("refresh_token", refreshToken);
        editor.apply();
    }

    private boolean validateInputs(String username, String password) {
        tilUsername.setError(null);
        tilPassword.setError(null);

        if (username.isEmpty()) {
            tilUsername.setError(getString(R.string.error_username_empty));
            return false;
        }
        if (password.isEmpty()) {
            tilPassword.setError(getString(R.string.error_password_empty));
            return false;
        }
        if (username.length() < 3 || username.length() > 20) {
            tilUsername.setError(getString(R.string.error_username_length));
            return false;
        }
        if (password.length() < 6 || password.length() > 20) {
            tilPassword.setError(getString(R.string.error_password_length));
            return false;
        }
        return true;
    }

    private void setupClickableAgreement() {
        String agreementText = "我已阅读并同意《用户协议》和《隐私策略》";
        SpannableString spannableString = new SpannableString(agreementText);

        ClickableSpan userAgreementSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(LoginActivity.this, WebViewActivity.class);
                intent.putExtra("url", "https://yyybbbyyyb.github.io");
                startActivity(intent);
            }
        };

        ClickableSpan privacyPolicySpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(LoginActivity.this, WebViewActivity.class);
                intent.putExtra("url", "https://github.com/yyybbbyyyb");
                startActivity(intent);
            }
        };

        //  设置《用户协议》和《隐私策略》为可点击
        spannableString.setSpan(userAgreementSpan, 7, 13, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(privacyPolicySpan, 14, 20, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        checkboxAgreement.setText(spannableString);
        checkboxAgreement.setMovementMethod(LinkMovementMethod.getInstance());  // 使链接可点击

    }
}
