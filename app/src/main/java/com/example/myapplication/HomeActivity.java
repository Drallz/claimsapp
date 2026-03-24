package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapters.DashboardAdapter;
import com.example.myapplication.DashboardItem;
import com.example.myapplication.models.User;
import com.example.myapplication.utils.SharedPrefManager;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private TextView tvWelcomeMessage;
    private RecyclerView rvDashboard;

    private SharedPrefManager sharedPrefManager;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeData();
        initializeViews();
        setupToolbar();
        setupNavigationDrawer();
        setupDashboard();
        updateWelcomeMessage();
    }

    private void initializeData() {
        sharedPrefManager = SharedPrefManager.getInstance(this);
        currentUser = sharedPrefManager.getUser();
    }

    private void initializeViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        tvWelcomeMessage = findViewById(R.id.tv_welcome_message);
        rvDashboard = findViewById(R.id.rv_dashboard);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Dashboard");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        }
    }

    private void updateWelcomeMessage() {
        if (currentUser != null) {
            String welcomeText = "Welcome, " + currentUser.getFirstName() + " " +
                    currentUser.getLastName() + "!";
            tvWelcomeMessage.setText(welcomeText);
        }
    }

    private void setupNavigationDrawer() {
        View headerView = navigationView.getHeaderView(0);
        TextView tvName = headerView.findViewById(R.id.tv_header_name);
        TextView tvEmail = headerView.findViewById(R.id.tv_header_email);

        if (currentUser != null) {
            tvName.setText(currentUser.getFullName());
            tvEmail.setText(currentUser.getEmail());
        }

        Menu menu = navigationView.getMenu();

        MenuItem adminItem = menu.findItem(R.id.nav_admin);
        if (adminItem != null) {
            adminItem.setVisible(currentUser != null && currentUser.isAdmin());
        }

        MenuItem managerItem = menu.findItem(R.id.nav_manager);
        if (managerItem != null) {
            managerItem.setVisible(currentUser != null &&
                    (currentUser.isManager() || currentUser.isAdmin()));
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                // Already on home
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
            } else if (id == R.id.nav_claims) {
                Intent intent = new Intent(HomeActivity.this, ClaimsFormActivity.class);
                intent.putExtra("user", currentUser);
                startActivity(intent);
            } else if (id == R.id.nav_timetable) {
                Intent intent = new Intent(HomeActivity.this, TLATimetableActivity.class);
                intent.putExtra("user", currentUser);
                intent.putExtra("isManager", currentUser != null &&
                        (currentUser.isManager() || currentUser.isAdmin()));
                startActivity(intent);
            } else if (id == R.id.nav_about) {
                startActivity(new Intent(HomeActivity.this, AboutActivity.class));
            } else if (id == R.id.nav_admin) {
                startActivity(new Intent(HomeActivity.this, AdminActivity.class));
            } else if (id == R.id.nav_manager) {
                Intent intent = new Intent(HomeActivity.this, ManagerActivity.class);
                intent.putExtra("user", currentUser);
                startActivity(intent);
            } else if (id == R.id.nav_logout) {
                logout();
            }

            drawerLayout.closeDrawers();
            return true;
        });
    }

    private void setupDashboard() {
        List<DashboardItem> items = new ArrayList<>();

        items.add(new DashboardItem("Claims Form", R.drawable.ic_claim,
                v -> {
                    Intent intent = new Intent(HomeActivity.this, ClaimsFormActivity.class);
                    intent.putExtra("user", currentUser);
                    startActivity(intent);
                }));

        items.add(new DashboardItem("Timetable", R.drawable.ic_schedule,
                v -> {
                    Intent intent = new Intent(HomeActivity.this, TLATimetableActivity.class);
                    intent.putExtra("user", currentUser);
                    intent.putExtra("isManager", currentUser != null &&
                            (currentUser.isManager() || currentUser.isAdmin()));
                    startActivity(intent);
                }));

        items.add(new DashboardItem("Profile", R.drawable.ic_profile,
                v -> startActivity(new Intent(HomeActivity.this, ProfileActivity.class))));

        items.add(new DashboardItem("About", R.drawable.ic_info,
                v -> startActivity(new Intent(HomeActivity.this, AboutActivity.class))));

        if (currentUser != null && currentUser.isAdmin()) {
            items.add(new DashboardItem("Admin Panel", R.drawable.ic_admin,
                    v -> startActivity(new Intent(HomeActivity.this, AdminActivity.class))));
        }

        if (currentUser != null && (currentUser.isManager() || currentUser.isAdmin())) {
            items.add(new DashboardItem("Manage Employees", R.drawable.ic_manager,
                    v -> {
                        Intent intent = new Intent(HomeActivity.this, ManagerActivity.class);
                        intent.putExtra("user", currentUser);
                        startActivity(intent);
                    }));
        }

        rvDashboard.setLayoutManager(new GridLayoutManager(this, 2));
        rvDashboard.setAdapter(new DashboardAdapter(items));
    }

    private void logout() {
        sharedPrefManager.clear();
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.open();
            return true;
        } else if (item.getItemId() == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}