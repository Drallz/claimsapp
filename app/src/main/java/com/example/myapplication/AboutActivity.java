package com.example.myapplication;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("About");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        TextView tvAbout = findViewById(R.id.tv_about);
        tvAbout.setText("Claims Management System\n\n" +
                "Version 1.0\n\n" +
                "This application allows TLAs to log their work hours and claims, " +
                "managers to review and approve claims, and administrators to manage " +
                "users and system settings.\n\n" +
                "© 2024 Mathematical Sciences Support");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
