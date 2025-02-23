package com.example.moviesapplication;

import android.app.Application;
import com.google.firebase.FirebaseApp;

public class MoviesApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }
} 