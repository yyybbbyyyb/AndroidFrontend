package com.example.myapplication.activity;

import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;

public class WebViewActivity extends AppCompatActivity{

    private WebView webView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        // 设置状态栏颜色
        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.white));

        webView = findViewById(R.id.web_view);
        webView.setWebViewClient(new WebViewClient()); // 设置在当前WebView继续加载网页

        String url = getIntent().getStringExtra("url");
        if (url != null) {
            webView.loadUrl(url);
        }
    }

}
