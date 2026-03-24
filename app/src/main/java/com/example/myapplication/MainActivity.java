package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private TextView tvForgotPassword;
    private TextView tvError;
    private ImageView ivLogo;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        initializeData();
        checkExistingSession();
        setupClickListeners();
    }

    private void initializeViews() {
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);
        tvError = findViewById(R.id.tv_error);
        ivLogo = findViewById(R.id.iv_logo);

        tvError.setVisibility(View.GONE);
    }

    private void initializeData() {
        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
    }

    private void checkExistingSession() {
        boolean isAuthenticated = sharedPreferences.getBoolean("isAuthenticated", false);

        if (isAuthenticated) {
            navigateToHome();
        }
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });



    }

    private void attemptLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (validateInputs(username, password)) {
            // Open Google OAuth
            openGoogleLogin();
        }
    }

    private boolean validateInputs(String username, String password) {
        if (username.isEmpty()) {
            etUsername.setError("Staff Number is required");
            etUsername.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void openGoogleLogin() {
        Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
        intent.putExtra("url", "http://10.0.2.2:3000/auth/google");
        startActivity(intent);
    }

    private void showError(String message) {
        tvError.setVisibility(View.VISIBLE);
        tvError.setText(message);
    }

    private void navigateToHome() {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}