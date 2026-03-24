package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myapplication.models.Claim;
import com.example.myapplication.models.User;
import com.example.myapplication.utils.ApiClient;
import com.example.myapplication.utils.DateUtils;
import com.example.myapplication.utils.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ClaimsFormActivity extends AppCompatActivity {

    private Spinner spinnerTeam, spinnerManager;
    private EditText etTask, etNotes, etDate, etHours;
    private Button btnAddClaim, btnSaveChanges, btnDelete;
    private TableLayout tableClaims;
    private TextView tvTotalHours, tvCurrentMonth;
    private Button btnPrevMonth, btnNextMonth;
    private ProgressBar progressBar;

    private ApiClient apiClient;
    private SharedPrefManager sharedPrefManager;
    private User currentUser;

    private List<Claim> claimsList = new ArrayList<>();
    private int selectedClaimIndex = -1;
    private Calendar currentMonth = Calendar.getInstance();
    private List<Claim.Claim> managers = new ArrayList<>();
    private String[] teamOptions = {
            "Business Continuity & Disaster Recovery",
            "Welcoming Team",
            "Collection Center",
            "Scientific Research Support",
            "Building Management",
            "Multimedia",
            "Business Solutions Research & Development",
            "Interest Group Research & Development",
            "Museum",
            "Fabrication Lab",
            "Competitive Interest Groups",
            "Special Event Support"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claims_form);

        initializeViews();
        initializeData();
        setupToolbar();
        setupSpinners();
        loadManagers();
        loadClaims();
        setupClickListeners();
        updateMonthDisplay();
    }

    private void initializeViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        spinnerTeam = findViewById(R.id.spinner_team);
        spinnerManager = findViewById(R.id.spinner_manager);
        etTask = findViewById(R.id.et_task);
        etNotes = findViewById(R.id.et_notes);
        etDate = findViewById(R.id.et_date);
        etHours = findViewById(R.id.et_hours);
        btnAddClaim = findViewById(R.id.btn_add_claim);
        btnSaveChanges = findViewById(R.id.btn_save_changes);
        btnDelete = findViewById(R.id.btn_delete);
        tableClaims = findViewById(R.id.table_claims);
        tvTotalHours = findViewById(R.id.tv_total_hours);
        tvCurrentMonth = findViewById(R.id.tv_current_month);
        btnPrevMonth = findViewById(R.id.btn_prev_month);
        btnNextMonth = findViewById(R.id.btn_next_month);
        progressBar = findViewById(R.id.progress_bar);

        // Set default date to today
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        etDate.setText(sdf.format(new Date()));

        // Set max date (today) and min date (3 days ago)
        Calendar cal = Calendar.getInstance();
        etDate.setMaxDate(cal.getTimeInMillis());
        cal.add(Calendar.DAY_OF_MONTH, -3);
        etDate.setMinDate(cal.getTimeInMillis());
    }

    private void initializeData() {
        apiClient = ApiClient.getInstance(this);
        sharedPrefManager = SharedPrefManager.getInstance(this);
        currentUser = sharedPrefManager.getUser();

        if (currentUser == null) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Claims Form");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupSpinners() {
        ArrayAdapter<String> teamAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, teamOptions);
        teamAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTeam.setAdapter(teamAdapter);
    }

    private void loadManagers() {
        apiClient.get("/api/managers", new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(() -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        List<String> managerNames = new ArrayList<>();
                        managers.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            Claim.Manager manager = new Claim.Manager();
                            manager.setManNumber(obj.getString("man_number"));
                            manager.setFirstName(obj.getString("first_name"));
                            manager.setLastName(obj.getString("last_name"));
                            managers.add(manager);
                            managerNames.add(manager.getFullName());
                        }

                        ArrayAdapter<String> managerAdapter = new ArrayAdapter<>(ClaimsFormActivity.this,
                                android.R.layout.simple_spinner_item, managerNames);
                        managerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerManager.setAdapter(managerAdapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public void onError(int code, String message) {
                runOnUiThread(() ->
                        Toast.makeText(ClaimsFormActivity.this,
                                "Error loading managers", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onFailure(Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(ClaimsFormActivity.this,
                                "Network error", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void loadClaims() {
        showProgress(true);

        String monthYear = String.format(Locale.getDefault(), "%d-%02d",
                currentMonth.get(Calendar.YEAR),
                currentMonth.get(Calendar.MONTH) + 1);

        apiClient.get("/api/claimsform/" + currentUser.getEmpNumber() + "?month=" + monthYear,
                new ApiClient.ApiCallback() {
                    @Override
                    public void onSuccess(String response) {
                        runOnUiThread(() -> {
                            showProgress(false);
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                if (jsonArray.length() > 0) {
                                    JSONArray claimsArray = jsonArray.getJSONArray(0);
                                    claimsList.clear();

                                    for (int i = 0; i < claimsArray.length(); i++) {
                                        JSONObject obj = claimsArray.getJSONObject(i);
                                        claimsList.add(Claim.fromJson(obj));
                                    }

                                    displayClaims();
                                    calculateTotalHours();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(ClaimsFormActivity.this,
                                        "Error parsing claims", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onError(int code, String message) {
                        runOnUiThread(() -> {
                            showProgress(false);
                            Toast.makeText(ClaimsFormActivity.this,
                                    "Error loading claims", Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onFailure(Exception e) {
                        runOnUiThread(() -> {
                            showProgress(false);
                            Toast.makeText(ClaimsFormActivity.this,
                                    "Network error", Toast.LENGTH_SHORT).show();
                        });
                    }
                });
    }

    private void displayClaims() {
        tableClaims.removeAllViews();

        // Add header
        TableRow headerRow = new TableRow(this);
        headerRow.addView(createTableCell("Team", true));
        headerRow.addView(createTableCell("Task", true));
        headerRow.addView(createTableCell("Notes", true));
        headerRow.addView(createTableCell("Manager", true));
        headerRow.addView(createTableCell("Date", true));
        headerRow.addView(createTableCell("Hours", true));
        tableClaims.addView(headerRow);

        // Add claim rows
        for (int i = 0; i < claimsList.size(); i++) {
            Claim claim = claimsList.get(i);
            TableRow row = new TableRow(this);
            int finalI = i;

            row.setOnClickListener(v -> selectClaim(finalI));
            row.setOnLongClickListener(v -> {
                editClaim(finalI);
                return true;
            });

            row.addView(createTableCell(claim.getTeam(), false));
            row.addView(createTableCell(claim.getTask(), false));
            row.addView(createTableCell(claim.getNotes(), false));
            row.addView(createTableCell(claim.getManagerFullName(), false));
            row.addView(createTableCell(claim.getDate(), false));
            row.addView(createTableCell(String.valueOf(claim.getHours()), false));

            if (i == selectedClaimIndex) {
                row.setBackgroundColor(0xFF64B5F6);
            }

            tableClaims.addView(row);
        }
    }

    private TextView createTableCell(String text, boolean isHeader) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setPadding(16, 8, 16, 8);
        if (isHeader) {
            tv.setTypeface(null, android.graphics.Typeface.BOLD);
            tv.setBackgroundColor(0xFF2196F3);
            tv.setTextColor(0xFFFFFFFF);
        }
        return tv;
    }

    private void calculateTotalHours() {
        double total = 0;
        for (Claim claim : claimsList) {
            total += claim.getHours();
        }
        tvTotalHours.setText(String.format("Total Hours: %.1f", total));
    }

    private void selectClaim(int index) {
        selectedClaimIndex = index;
        displayClaims();
        btnDelete.setEnabled(true);
    }

    private void editClaim(int index) {
        selectedClaimIndex = index;
        Claim claim = claimsList.get(index);

        // Populate form with claim data
        int teamIndex = -1;
        for (int i = 0; i < teamOptions.length; i++) {
            if (teamOptions[i].equals(claim.getTeam())) {
                teamIndex = i;
                break;
            }
        }
        if (teamIndex >= 0) {
            spinnerTeam.setSelection(teamIndex);
        }

        etTask.setText(claim.getTask());
        etNotes.setText(claim.getNotes());

        // Find manager index
        for (int i = 0; i < managers.size(); i++) {
            if (managers.get(i).getManNumber().equals(claim.getManNumber())) {
                spinnerManager.setSelection(i);
                break;
            }
        }

        etDate.setText(claim.getDate());
        etHours.setText(String.valueOf(claim.getHours()));

        btnAddClaim.setVisibility(View.GONE);
        btnSaveChanges.setVisibility(View.VISIBLE);
        btnSaveChanges.setTag(claim.getClaimNum());
    }

    private void setupClickListeners() {
        btnAddClaim.setOnClickListener(v -> addClaim());
        btnSaveChanges.setOnClickListener(v -> updateClaim());
        btnDelete.setOnClickListener(v -> deleteClaim());
        btnPrevMonth.setOnClickListener(v -> changeMonth(-1));
        btnNextMonth.setOnClickListener(v -> changeMonth(1));
    }

    private void addClaim() {
        if (!validateInputs()) return;

        JSONObject body = new JSONObject();
        try {
            body.put("emp_number", currentUser.getEmpNumber());
            body.put("man_number", managers.get(spinnerManager.getSelectedItemPosition()).getManNumber());
            body.put("team", teamOptions[spinnerTeam.getSelectedItemPosition()]);
            body.put("task", etTask.getText().toString().trim());
            body.put("notes", etNotes.getText().toString().trim());
            body.put("date", etDate.getText().toString().trim());
            body.put("hours", Double.parseDouble(etHours.getText().toString().trim()));
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        showProgress(true);

        apiClient.post("/api/claimsform", body, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(() -> {
                    showProgress(false);
                    Toast.makeText(ClaimsFormActivity.this,
                            "Claim added successfully", Toast.LENGTH_SHORT).show();
                    clearForm();
                    loadClaims();
                });
            }

            @Override
            public void onError(int code, String message) {
                runOnUiThread(() -> {
                    showProgress(false);
                    Toast.makeText(ClaimsFormActivity.this,
                            "Error: " + message, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFailure(Exception e) {
                runOnUiThread(() -> {
                    showProgress(false);
                    Toast.makeText(ClaimsFormActivity.this,
                            "Network error", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void updateClaim() {
        if (selectedClaimIndex < 0) return;

        JSONObject body = new JSONObject();
        try {
            JSONArray claimsArray = new JSONArray();
            Claim claim = claimsList.get(selectedClaimIndex);

            JSONObject claimObj = new JSONObject();
            claimObj.put("claim_num", claim.getClaimNum());
            claimObj.put("team", teamOptions[spinnerTeam.getSelectedItemPosition()]);
            claimObj.put("task", etTask.getText().toString().trim());
            claimObj.put("notes", etNotes.getText().toString().trim());
            claimObj.put("hours", Double.parseDouble(etHours.getText().toString().trim()));

            claimsArray.put(claimObj);
            body.put("claims", claimsArray);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        showProgress(true);

        apiClient.post("/api/claimsform/update-claims", body, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(() -> {
                    showProgress(false);
                    Toast.makeText(ClaimsFormActivity.this,
                            "Claim updated successfully", Toast.LENGTH_SHORT).show();
                    btnAddClaim.setVisibility(View.VISIBLE);
                    btnSaveChanges.setVisibility(View.GONE);
                    clearForm();
                    loadClaims();
                });
            }

            @Override
            public void onError(int code, String message) {
                runOnUiThread(() -> {
                    showProgress(false);
                    Toast.makeText(ClaimsFormActivity.this,
                            "Error: " + message, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFailure(Exception e) {
                runOnUiThread(() -> {
                    showProgress(false);
                    Toast.makeText(ClaimsFormActivity.this,
                            "Network error", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void deleteClaim() {
        if (selectedClaimIndex < 0) return;

        Claim claim = claimsList.get(selectedClaimIndex);

        showProgress(true);

        apiClient.delete("/api/claimsform/" + claim.getClaimNum(), new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(() -> {
                    showProgress(false);
                    Toast.makeText(ClaimsFormActivity.this,
                            "Claim deleted successfully", Toast.LENGTH_SHORT).show();
                    selectedClaimIndex = -1;
                    btnDelete.setEnabled(false);
                    loadClaims();
                });
            }

            @Override
            public void onError(int code, String message) {
                runOnUiThread(() -> {
                    showProgress(false);
                    Toast.makeText(ClaimsFormActivity.this,
                            "Error: " + message, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFailure(Exception e) {
                runOnUiThread(() -> {
                    showProgress(false);
                    Toast.makeText(ClaimsFormActivity.this,
                            "Network error", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private boolean validateInputs() {
        if (spinnerTeam.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select a team", Toast.LENGTH_SHORT).show();
            return false;
        }

        String task = etTask.getText().toString().trim();
        if (task.isEmpty()) {
            etTask.setError("Task is required");
            return false;
        }

        String notes = etNotes.getText().toString().trim();
        if (notes.isEmpty()) {
            etNotes.setError("Notes are required");
            return false;
        }

        if (notes.split("\\s+").length < 15) {
            etNotes.setError("Notes must be at least 15 words");
            return false;
        }

        if (spinnerManager.getSelectedItemPosition() < 0) {
            Toast.makeText(this, "Please select a manager", Toast.LENGTH_SHORT).show();
            return false;
        }

        String hoursStr = etHours.getText().toString().trim();
        if (hoursStr.isEmpty()) {
            etHours.setError("Hours are required");
            return false;
        }

        double hours = Double.parseDouble(hoursStr);
        if (hours <= 0 || hours > 3) {
            etHours.setError("Hours must be between 0.5 and 3");
            return false;
        }

        return true;
    }

    private void clearForm() {
        spinnerTeam.setSelection(0);
        etTask.setText("");
        etNotes.setText("");
        etHours.setText("");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        etDate.setText(sdf.format(new Date()));

        selectedClaimIndex = -1;
        btnAddClaim.setVisibility(View.VISIBLE);
        btnSaveChanges.setVisibility(View.GONE);
        btnDelete.setEnabled(false);
    }

    private void changeMonth(int delta) {
        currentMonth.add(Calendar.MONTH, delta);
        updateMonthDisplay();
        loadClaims();
    }

    private void updateMonthDisplay() {
        String monthName = currentMonth.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        int year = currentMonth.get(Calendar.YEAR);
        tvCurrentMonth.setText(String.format("%s %d", monthName, year));
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