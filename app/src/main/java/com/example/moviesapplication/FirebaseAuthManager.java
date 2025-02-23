package com.example.moviesapplication;

import android.content.Context;
import android.content.SharedPreferences;

public class FirebaseAuthManager {
    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_PHONE = "phone";
    
    private static Users currentUser;
    private static SharedPreferences prefs;

    public static void init(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        loadUserFromPrefs();
    }

    public static void setCurrentUser(Users user) {
        currentUser = user;
        if (user != null && prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(KEY_USERNAME, user.getUserName());
            editor.putString(KEY_PASSWORD, user.getPassword());
            editor.putString(KEY_PHONE, user.getPhoneNumber());
            editor.apply();
        }
    }

    public static Users getCurrentUser() {
        if (currentUser == null && prefs != null) {
            loadUserFromPrefs();
        }
        return currentUser;
    }

    private static void loadUserFromPrefs() {
        if (prefs != null && prefs.contains(KEY_USERNAME)) {
            String username = prefs.getString(KEY_USERNAME, "");
            String password = prefs.getString(KEY_PASSWORD, "");
            String phone = prefs.getString(KEY_PHONE, "");
            currentUser = new Users(username, password, phone);
        }
    }

    public static void logout() {
        currentUser = null;
        if (prefs != null) {
            prefs.edit().clear().apply();
        }
    }
}
