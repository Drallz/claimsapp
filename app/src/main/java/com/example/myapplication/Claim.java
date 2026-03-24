package com.example.myapplication.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Claim {
    private int claimNum;
    private String empNumber;
    private String team;
    private String task;
    private String notes;
    private String manNumber;
    private String managerFirstName;
    private String managerLastName;
    private String date;
    private double hours;

    public Claim() {
    }

    public static Claim fromJson(JSONObject json) throws JSONException {
        Claim claim = new Claim();

        if (json.has("claim_num")) {
            claim.setClaimNum(json.getInt("claim_num"));
        }
        if (json.has("emp_number")) {
            claim.setEmpNumber(json.getString("emp_number"));
        }
        if (json.has("team")) {
            claim.setTeam(json.getString("team"));
        }
        if (json.has("task")) {
            claim.setTask(json.getString("task"));
        }
        if (json.has("notes")) {
            claim.setNotes(json.getString("notes"));
        }
        if (json.has("man_number")) {
            claim.setManNumber(json.getString("man_number"));
        }
        if (json.has("first_name")) {
            claim.setManagerFirstName(json.getString("first_name"));
        }
        if (json.has("last_name")) {
            claim.setManagerLastName(json.getString("last_name"));
        }
        if (json.has("date")) {
            String dateStr = json.getString("date");
            claim.setDate(dateStr.split("T")[0]); // Get just the date part
        }
        if (json.has("hours")) {
            claim.setHours(json.getDouble("hours"));
        }

        return claim;
    }

    // Getters and Setters
    public int getClaimNum() { return claimNum; }
    public void setClaimNum(int claimNum) { this.claimNum = claimNum; }

    public String getEmpNumber() { return empNumber; }
    public void setEmpNumber(String empNumber) { this.empNumber = empNumber; }

    public String getTeam() { return team; }
    public void setTeam(String team) { this.team = team; }

    public String getTask() { return task; }
    public void setTask(String task) { this.task = task; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getManNumber() { return manNumber; }
    public void setManNumber(String manNumber) { this.manNumber = manNumber; }

    public String getManagerFirstName() { return managerFirstName; }
    public void setManagerFirstName(String managerFirstName) { this.managerFirstName = managerFirstName; }

    public String getManagerLastName() { return managerLastName; }
    public void setManagerLastName(String managerLastName) { this.managerLastName = managerLastName; }

    public String getManagerFullName() {
        return (managerFirstName != null ? managerFirstName : "") + " " +
                (managerLastName != null ? managerLastName : "");
    }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public double getHours() { return hours; }
    public void setHours(double hours) { this.hours = hours; }
}