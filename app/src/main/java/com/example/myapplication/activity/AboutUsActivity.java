package com.example.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;

public class AboutUsActivity extends AppCompatActivity {

    ImageView returnButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        // 设置状态栏颜色
        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.white));


        returnButton = findViewById(R.id.btn_back);
        returnButton.setOnClickListener(v -> finish());

        LinearLayout userAgreement = findViewById(R.id.user_agreement);
        userAgreement.setOnClickListener(v -> {
            Intent intent = new Intent(AboutUsActivity.this, WebViewActivity.class);
            intent.putExtra("url", "https://yyybbbyyyb.github.io");
            startActivity(intent);
        });

        LinearLayout privacyPolicy = findViewById(R.id.privacy_policy);
        privacyPolicy.setOnClickListener(v -> {
            Intent intent = new Intent(AboutUsActivity.this, WebViewActivity.class);
            intent.putExtra("url", "https://github.com/yyybbbyyyb");
            startActivity(intent);
        });
    }
}
