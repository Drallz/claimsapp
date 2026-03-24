package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myapplication.models.User;
import com.example.myapplication.utils.ApiClient;
import com.example.myapplication.utils.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvName, tvEmployeeNumber, tvManager, tvRate, tvIdNumber,
            tvYearOfStudy, tvPhoneNumber, tvEmail, tvStatus;
    private EditText etNewPassword, etConfirmPassword;
    private Button btnChangePassword, btnUpdatePassword, btnCancelPassword;
    private ProgressBar progressBar;
    private View passwordInputSection;

    private ApiClient apiClient;
    private SharedPrefManager sharedPrefManager;
    private User currentUser;
    private boolean isManager = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initializeViews();
        initializeData();
        setupToolbar();
        loadUserData();
        setupClickListeners();
    }

    private void initializeViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvName = findViewById(R.id.tv_name);
        tvEmployeeNumber = findViewById(R.id.tv_employee_number);
        tvManager = findViewById(R.id.tv_manager);
        tvRate = findViewById(R.id.tv_rate);
        tvIdNumber = findViewById(R.id.tv_id_number);
        tvYearOfStudy = findViewById(R.id.tv_year_of_study);
        tvPhoneNumber = findViewById(R.id.tv_phone_number);
        tvEmail = findViewById(R.id.tv_email);
        tvStatus = findViewById(R.id.tv_status);

        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnChangePassword = findViewById(R.id.btn_change_password);
        btnUpdatePassword = findViewById(R.id.btn_update_password);
        btnCancelPassword = findViewById(R.id.btn_cancel_password);
        passwordInputSection = findViewById(R.id.password_input_section);
        progressBar = findViewById(R.id.progress_bar);

        passwordInputSection.setVisibility(View.GONE);
    }

    private void initializeData() {
        apiClient = ApiClient.getInstance(this);
        sharedPrefManager = SharedPrefManager.getInstance(this);
        currentUser = sharedPrefManager.getUser();

        // Check if user is manager from intent
        isManager = getIntent().getBooleanExtra("isManager", false);
    }

    private void setupToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My Profile");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void loadUserData() {
        if (currentUser == null) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        showProgress(true);

        // Fetch latest user data from server
        apiClient.get("/api/profile/" + currentUser.getEmpNumber(), new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(() -> {
                    showProgress(false);
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        if (jsonArray.length() > 0) {
                            JSONArray innerArray = jsonArray.getJSONArray(0);
                            if (innerArray.length() > 0) {
                                JSONObject userData = innerArray.getJSONObject(0);
                                displayUserData(User.fromJson(userData));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ProfileActivity.this,
                                "Error parsing user data", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(int code, String message) {
                runOnUiThread(() -> {
                    showProgress(false);
                    Toast.makeText(ProfileActivity.this,
                            "Error: " + message, Toast.LENGTH_SHORT).show();
                    // Fallback to cached user data
                    displayUserData(currentUser);
                });
            }

            @Override
            public void onFailure(Exception e) {
                runOnUiThread(() -> {
                    showProgress(false);
                    Toast.makeText(ProfileActivity.this,
                            "Network error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    displayUserData(currentUser);
                });
            }
        });

        // Fetch submission status
        apiClient.get("/api/claimsform/submission-status/" + currentUser.getEmpNumber(),
                new ApiClient.ApiCallback() {
                    @Override
                    public void onSuccess(String response) {
                        runOnUiThread(() -> {
                            try {
                                JSONObject statusJson = new JSONObject(response);
                                String status = statusJson.optString("status", "No Submission yet");
                                tvStatus.setText("Status: " + status);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        });
                    }

                    @Override
                    public void onError(int code, String message) {
                        tvStatus.setText("Status: Unknown");
                    }

                    @Override
                    public void onFailure(Exception e) {
                        tvStatus.setText("Status: Unknown");
                    }
                });
    }

    private void displayUserData(User user) {
        tvName.setText(user.getFullName());
        tvEmployeeNumber.setText("Employee #: " + user.getEmpNumber());
        tvManager.setText("Manager: " + user.getManagerFullName());
        tvRate.setText("Rate: R" + String.format("%.2f", user.getRate()) + "/hour");
        tvIdNumber.setText("ID: " + (user.getIdNumber() != null ? user.getIdNumber() : "N/A"));
        tvYearOfStudy.setText("Year of Study: " + user.getYearOfStudy());

        String phone = user.getPhoneNumber();
        if (phone != null && !phone.isEmpty()) {
            if (!phone.startsWith("0")) {
                phone = "0" + phone;
            }
            tvPhoneNumber.setText("Phone: " + phone);
        } else {
            tvPhoneNumber.setText("Phone: N/A");
        }

        tvEmail.setText("Email: " + (user.getEmail() != null ? user.getEmail() : "N/A"));

        // Show rate edit for managers
        if (isManager) {
            findViewById(R.id.rate_edit_section).setVisibility(View.VISIBLE);
        }
    }

    private void setupClickListeners() {
        btnChangePassword.setOnClickListener(v -> {
            passwordInputSection.setVisibility(View.VISIBLE);
            btnChangePassword.setVisibility(View.GONE);
        });

        btnUpdatePassword.setOnClickListener(v -> updatePassword());

        btnCancelPassword.setOnClickListener(v -> {
            passwordInputSection.setVisibility(View.GONE);
            btnChangePassword.setVisibility(View.VISIBLE);
            etNewPassword.setText("");
            etConfirmPassword.setText("");
        });
    }

    private void updatePassword() {
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (newPassword.isEmpty()) {
            etNewPassword.setError("Password is required");
            return;
        }

        if (newPassword.length() < 8) {
            etNewPassword.setError("Password must be at least 8 characters");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            return;
        }

        showProgress(true);

        JSONObject body = new JSONObject();
        try {
            body.put("emp_number", currentUser.getEmpNumber());
            body.put("newPassword", newPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        apiClient.post("/api/profile/update-password", body, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(() -> {
                    showProgress(false);
                    Toast.makeText(ProfileActivity.this,
                            "Password updated successfully", Toast.LENGTH_LONG).show();
                    passwordInputSection.setVisibility(View.GONE);
                    btnChangePassword.setVisibility(View.VISIBLE);
                    etNewPassword.setText("");
                    etConfirmPassword.setText("");
                });
            }

            @Override
            public void onError(int code, String message) {
                runOnUiThread(() -> {
                    showProgress(false);
                    Toast.makeText(ProfileActivity.this,
                            "Failed to update password: " + message, Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onFailure(Exception e) {
                runOnUiThread(() -> {
                    showProgress(false);
                    Toast.makeText(ProfileActivity.this,
                            "Network error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
