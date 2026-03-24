package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myapplication.utils.ApiClient;
import com.example.myapplication.utils.SharedPrefManager;

public class WebViewActivity extends AppCompatActivity {

    private WebView webView;
    private Toolbar toolbar;
    private ApiClient apiClient;
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        initializeViews();
        initializeData();
        loadUrl();
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Google Login");
        }

        webView = findViewById(R.id.webview);
    }

    private void initializeData() {
        apiClient = ApiClient.getInstance(this);
        sharedPrefManager = SharedPrefManager.getInstance(this);
    }

    private void loadUrl() {
        String url = getIntent().getStringExtra("url");
        if (url == null) {
            finish();
            return;
        }

        setupWebView();
        webView.loadUrl(url);
    }

    private void setupWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("localhost:3000") || url.contains("your-redirect-url")) {
                    fetchUserData();
                    return true;
                }
                return false;
            }
        });
    }

    private void fetchUserData() {
        apiClient.checkSession(new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(() -> {
                    Toast.makeText(WebViewActivity.this,
                            "Login successful!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(WebViewActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
            }

            @Override
            public void onError(int code, String message) {
                runOnUiThread(() -> {
                    Toast.makeText(WebViewActivity.this,
                            "Login failed: " + message, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFailure(Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(WebViewActivity.this,
                            "Network error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}