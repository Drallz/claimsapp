package com.example.myapplication.models;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    private String empNumber;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private String managerNumber;
    private String managerFirstName;
    private String managerLastName;
    private double rate;
    private String idNumber;
    private String phoneNumber;
    private int yearOfStudy;
    private boolean extraHours;

    public User() {
    }

    public static User fromJson(JSONObject json) throws JSONException {
        User user = new User();

        if (json.has("emp_number")) {
            user.setEmpNumber(json.getString("emp_number"));
        }
        if (json.has("first_name")) {
            user.setFirstName(json.getString("first_name"));
        }
        if (json.has("last_name")) {
            user.setLastName(json.getString("last_name"));
        }
        if (json.has("Email") || json.has("email")) {
            user.setEmail(json.optString("Email", json.optString("email", "")));
        }
        if (json.has("role")) {
            user.setRole(json.getString("role"));
        } else {
            // Default role based on your database structure
            user.setRole("employee");
        }
        if (json.has("man_number")) {
            user.setManagerNumber(json.getString("man_number"));
        }
        if (json.has("manager_first_name")) {
            user.setManagerFirstName(json.getString("manager_first_name"));
        }
        if (json.has("manager_last_name")) {
            user.setManagerLastName(json.getString("manager_last_name"));
        }
        if (json.has("rate")) {
            user.setRate(json.getDouble("rate"));
        }
        if (json.has("ID_number")) {
            user.setIdNumber(json.getString("ID_number"));
        }
        if (json.has("Phone_number")) {
            user.setPhoneNumber(json.getString("Phone_number"));
        }
        if (json.has("Year_of_Study")) {
            user.setYearOfStudy(json.getInt("Year_of_Study"));
        }
        if (json.has("extra_hours")) {
            user.setExtraHours(json.getBoolean("extra_hours"));
        }

        return user;
    }

    // Getters and Setters
    public String getEmpNumber() { return empNumber; }
    public void setEmpNumber(String empNumber) { this.empNumber = empNumber; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFullName() { return firstName + " " + lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getManagerNumber() { return managerNumber; }
    public void setManagerNumber(String managerNumber) { this.managerNumber = managerNumber; }

    public String getManagerFirstName() { return managerFirstName; }
    public void setManagerFirstName(String managerFirstName) { this.managerFirstName = managerFirstName; }

    public String getManagerLastName() { return managerLastName; }
    public void setManagerLastName(String managerLastName) { this.managerLastName = managerLastName; }

    public String getManagerFullName() {
        return (managerFirstName != null ? managerFirstName : "") + " " +
                (managerLastName != null ? managerLastName : "");
    }

    public double getRate() { return rate; }
    public void setRate(double rate) { this.rate = rate; }

    public String getIdNumber() { return idNumber; }
    public void setIdNumber(String idNumber) { this.idNumber = idNumber; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public int getYearOfStudy() { return yearOfStudy; }
    public void setYearOfStudy(int yearOfStudy) { this.yearOfStudy = yearOfStudy; }

    public boolean isExtraHours() { return extraHours; }
    public void setExtraHours(boolean extraHours) { this.extraHours = extraHours; }

    // Role checking methods (like your protectedRoute)
    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(role);
    }

    public boolean isManager() {
        return "manager".equalsIgnoreCase(role);
    }

    public boolean isEmployee() {
        return "employee".equalsIgnoreCase(role) || role == null;
    }

    public boolean hasRole(String role) {
        return this.role != null && this.role.equalsIgnoreCase(role);
    }

    public boolean hasAnyRole(String[] roles) {
        if (role == null) return false;
        for (String r : roles) {
            if (role.equalsIgnoreCase(r)) return true;
        }
        return false;
    }
}