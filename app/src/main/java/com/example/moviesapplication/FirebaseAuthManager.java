package com.example.moviesapplication;

import com.example.moviesapplication.model.Users;

public class FirebaseAuthManager {
    private static String currentUsername;
    private static Users currentUser;

    public static void setCurrentUser(Users user) {
        currentUser = user;
        currentUsername = user.getUserName();
    }

    public static Users getCurrentUser() {
        return currentUser;
    }
}
