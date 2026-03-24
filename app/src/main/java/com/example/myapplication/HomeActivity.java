package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private TextView tvWelcomeMessage;
    private RecyclerView rvDashboard;
    private SimpleSharedPrefManager sharedPrefManager;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initializeViews();
        initializeData();
        setupDashboard();
        updateWelcomeMessage();
    }

    private void initializeViews() {
        tvWelcomeMessage = findViewById(R.id.tv_welcome_message);
        rvDashboard = findViewById(R.id.rv_dashboard);
    }

    private void initializeData() {
        sharedPrefManager = SharedPrefManager.getInstance(this);
        currentUser = sharedPrefManager.getUser();
    }

    private void updateWelcomeMessage() {
        if (currentUser != null) {
            String welcomeText = "Welcome, " + currentUser.getFullName() + "!";
            tvWelcomeMessage.setText(welcomeText);
        }
    }

    private void setupDashboard() {
        List<DashboardItem> items = new ArrayList<>();

        // Add dashboard items
        items.add(new DashboardItem("Profile", android.R.drawable.ic_menu_gallery,
                v -> Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show()));

        items.add(new DashboardItem("Settings", android.R.drawable.ic_menu_preferences,
                v -> Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show()));

        items.add(new DashboardItem("About", android.R.drawable.ic_menu_info_details,
                v -> Toast.makeText(this, "About clicked", Toast.LENGTH_SHORT).show()));

        items.add(new DashboardItem("Logout", android.R.drawable.ic_menu_close_clear_cancel,
                v -> logout()));

        rvDashboard.setLayoutManager(new GridLayoutManager(this, 2));
      //  rvDashboard.setAdapter(new DashboardAdapter(items));
    }

    private void logout() {
        //sharedPrefManager.clear();
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}