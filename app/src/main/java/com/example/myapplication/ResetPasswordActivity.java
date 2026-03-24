package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import org.json.JSONException;
import org.json.JSONObject;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etOtp;
    private EditText etNewPassword;
    private Button btnSendOtp;
    private Button btnResetPassword;
    private TextView tvMessage;
    private View otpSection;
    private View passwordSection;


    private int step = 1; // 1: email, 2: otp + password

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        initializeViews();
        initializeData();
        setupClickListeners();
    }

    private void initializeViews() {
        etEmail = findViewById(R.id.et_email);
        etOtp = findViewById(R.id.et_otp);
        etNewPassword = findViewById(R.id.et_new_password);
        btnSendOtp = findViewById(R.id.btn_send_otp);
        btnResetPassword = findViewById(R.id.btn_reset_password);
        tvMessage = findViewById(R.id.tv_message);
        otpSection = findViewById(R.id.otp_section);
        passwordSection = findViewById(R.id.password_section);

        otpSection.setVisibility(View.GONE);
        passwordSection.setVisibility(View.GONE);
        btnResetPassword.setVisibility(View.GONE);
    }

    private void initializeData() {
        apiClient = ApiClient.getInstance(this);
    }

    private void setupClickListeners() {
        btnSendOtp.setOnClickListener(v -> handleSendOtp());
        btnResetPassword.setOnClickListener(v -> handleResetPassword());
    }

    private void handleSendOtp() {
        String email = etEmail.getText().toString().trim();

        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            return;
        }

        JSONObject body = new JSONObject();
        try {
            body.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        apiClient.post("/api/forgot-password/send-otp", body, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(() -> {
                    tvMessage.setText("OTP sent to your email");
                    tvMessage.setVisibility(View.VISIBLE);
                    step = 2;
                    otpSection.setVisibility(View.VISIBLE);
                    passwordSection.setVisibility(View.VISIBLE);
                    btnResetPassword.setVisibility(View.VISIBLE);
                    btnSendOtp.setEnabled(false);
                });
            }

            @Override
            public void onError(int code, String message) {
                runOnUiThread(() -> {
                    tvMessage.setText("Failed to send OTP: " + message);
                    tvMessage.setVisibility(View.VISIBLE);
                });
            }

            @Override
            public void onFailure(Exception e) {
                runOnUiThread(() -> {
                    tvMessage.setText("Network error: " + e.getMessage());
                    tvMessage.setVisibility(View.VISIBLE);
                });
            }
        });
    }

    private void handleResetPassword() {
        String email = etEmail.getText().toString().trim();
        String otp = etOtp.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();

        if (otp.isEmpty()) {
            etOtp.setError("OTP is required");
            return;
        }

        if (newPassword.isEmpty()) {
            etNewPassword.setError("New password is required");
            return;
        }

        if (newPassword.length() < 8) {
            etNewPassword.setError("Password must be at least 8 characters");
            return;
        }

        JSONObject body = new JSONObject();
        try {
            body.put("email", email);
            body.put("otp", otp);
            body.put("password", newPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        apiClient.post("/api/forgot-password/verify-otp", body, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(() -> {
                    Toast.makeText(ResetPasswordActivity.this,
                            "Password reset successful", Toast.LENGTH_LONG).show();
                    finish();
                });
            }

            @Override
            public void onError(int code, String message) {
                runOnUiThread(() -> {
                    tvMessage.setText("Failed to reset password: " + message);
                    tvMessage.setVisibility(View.VISIBLE);
                });
            }

            @Override
            public void onFailure(Exception e) {
                runOnUiThread(() -> {
                    tvMessage.setText("Network error: " + e.getMessage());
                    tvMessage.setVisibility(View.VISIBLE);
                });
            }
        });
    }
}