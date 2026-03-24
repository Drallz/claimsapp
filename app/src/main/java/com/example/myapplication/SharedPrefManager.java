package com.example.myapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.myapplication.models.User;

import org.json.JSONException;
import org.json.JSONObject;

public class SharedPrefManager {
    private static final String PREF_NAME = "app_prefs";
    private static final String KEY_IS_AUTHENTICATED = "isAuthenticated";
    private static final String KEY_USER_JSON = "user_json";

    private static SharedPrefManager instance;
    private final SharedPreferences sharedPreferences;

    private SharedPrefManager(Context context) {
        sharedPreferences = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefManager(context);
        }
        return instance;
    }

    public void saveUser(User user, boolean isAuthenticated) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_AUTHENTICATED, isAuthenticated);

        JSONObject userJson = new JSONObject();
        try {
            userJson.put("emp_number", user.getEmpNumber());
            userJson.put("first_name", user.getFirstName());
            userJson.put("last_name", user.getLastName());
            userJson.put("email", user.getEmail());
            userJson.put("role", user.getRole());
            userJson.put("rate", user.getRate());
            userJson.put("ID_number", user.getIdNumber());
            userJson.put("Phone_number", user.getPhoneNumber());
            userJson.put("Year_of_Study", user.getYearOfStudy());
            userJson.put("man_number", user.getManagerNumber());
            userJson.put("manager_first_name", user.getManagerFirstName());
            userJson.put("manager_last_name", user.getManagerLastName());
            userJson.put("extra_hours", user.isExtraHours());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        editor.putString(KEY_USER_JSON, userJson.toString());
        editor.apply();
    }

    public void saveUserFromJson(String userJsonString, boolean isAuthenticated) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_AUTHENTICATED, isAuthenticated);
        editor.putString(KEY_USER_JSON, userJsonString);
        editor.apply();
    }

    public User getUser() {
        String userJsonString = sharedPreferences.getString(KEY_USER_JSON, null);
        if (userJsonString == null) return null;

        try {
            JSONObject userJson = new JSONObject(userJsonString);
            return User.fromJson(userJson);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isAuthenticated() {
        return sharedPreferences.getBoolean(KEY_IS_AUTHENTICATED, false);
    }

    public void clear() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public boolean hasRole(String role) {
        User user = getUser();
        return user != null && user.hasRole(role);
    }

    public boolean hasAnyRole(String[] allowedRoles) {
        User user = getUser();
        return user != null && user.hasAnyRole(allowedRoles);
    }

    public void saveString(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    public String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    public void saveBoolean(String key, boolean value) {
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public void remove(String key) {
        sharedPreferences.edit().remove(key).apply();
    }
}