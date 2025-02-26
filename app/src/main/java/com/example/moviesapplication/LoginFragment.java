package com.example.moviesapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginFragment extends Fragment {

    private EditText etUsername, etPassword;
    private Button btnLogin, btnRegister;
    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Firebase Database Reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        etUsername = view.findViewById(R.id.et_username);
        etPassword = view.findViewById(R.id.et_password);
        btnLogin = view.findViewById(R.id.btn_login);
        btnRegister = view.findViewById(R.id.btn_register);
        btnLogin.setOnClickListener(v -> loginUser(view));
        btnRegister.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_registerFragment)
        );
        return view;
    }

    private void loginUser(View view) {
        String userName = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(userName)) {
            etUsername.setError("User name is required");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            return;
        }

        // Query Firebase Realtime Database
        databaseReference.orderByChild("userName").equalTo(userName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Loop through results (should be only one)
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String dbPassword = snapshot.child("password").getValue(String.class);
                                if (dbPassword != null && dbPassword.equals(password)) {

                                    Users currentUser = new Users(userName, password, snapshot.child("phoneNumber").getValue(String.class));
                                    FirebaseAuthManager.setCurrentUser(currentUser);

                                    Toast.makeText(getActivity(), "Login successful!", Toast.LENGTH_SHORT).show();
                                    Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_allMoviesFragment);


                                } else {
                                    Toast.makeText(getActivity(), "Incorrect password!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(getActivity(), "User not found!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getActivity(), "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}