package com.example.moviesapplication;

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
