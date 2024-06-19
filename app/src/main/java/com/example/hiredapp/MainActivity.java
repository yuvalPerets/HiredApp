package com.example.hiredapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private LinearLayout signUpForm;
    private LinearLayout signInForm;
    private Button signUpButton;
    private Button signInButton;

    private String username;
    private String password;
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String USERNAME_KEY = "username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signUpForm = findViewById(R.id.signUpForm);
        signInForm = findViewById(R.id.signInForm);
        signUpButton = findViewById(R.id.signUpButton);
        signInButton = findViewById(R.id.signInButton);

        setupListeners();
    }

    private void setupListeners() {
        signUpButton.setOnClickListener(v -> toggleForms(true));

        signInButton.setOnClickListener(v -> toggleForms(false));

        Button signUpSubmitButton = findViewById(R.id.signupSubmitButton);
        signUpSubmitButton.setOnClickListener(v -> handleSignUp());

        Button signInSubmitButton = findViewById(R.id.signinSubmitButton);
        signInSubmitButton.setOnClickListener(v -> handleSignIn());
    }

    private void toggleForms(boolean isSignUp) {
        if (isSignUp) {
            signUpForm.setVisibility(View.VISIBLE);
            signInForm.setVisibility(View.GONE);
            signUpButton.setVisibility(View.GONE);
            signInButton.setVisibility(View.VISIBLE);
            setButtonTopMargin(signInButton, R.dimen.margin_top_300dp);
        } else {
            signInForm.setVisibility(View.VISIBLE);
            signUpForm.setVisibility(View.GONE);
            signInButton.setVisibility(View.GONE);
            signUpButton.setVisibility(View.VISIBLE);
        }
    }

    private void setButtonTopMargin(Button button, int marginTopDimenResId) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) button.getLayoutParams();
        params.topMargin = (int) getResources().getDimension(marginTopDimenResId);
        button.setLayoutParams(params);
    }

    private void handleSignUp() {
        EditText editUsername = findViewById(R.id.signupUsername);
        EditText editPassword = findViewById(R.id.signupPassword);
        username = editUsername.getText().toString();
        password = editPassword.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(MainActivity.this, "Username and Password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(username)) {
                    Toast.makeText(MainActivity.this, "Username already exists", Toast.LENGTH_SHORT).show();
                } else {
                    usersRef.child(username).setValue(password).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                            // Save username in SharedPreferences
                            saveUsernameAndNavigate(username);
                        } else {
                            Toast.makeText(MainActivity.this, "Registration failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Registration failed", task.getException());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Database error", error.toException());
            }
        });
    }

    private void handleSignIn() {
        EditText editUsername = findViewById(R.id.signinUsername);
        EditText editPassword = findViewById(R.id.signinPassword);
        username = editUsername.getText().toString();
        password = editPassword.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(MainActivity.this, "Username and Password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(username)) {
                    String storedPassword = snapshot.child(username).getValue(String.class);
                    if (storedPassword != null && storedPassword.equals(password)) {
                        Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        // Save username in SharedPreferences
                        saveUsernameAndNavigate(username);
                    } else {
                        Toast.makeText(MainActivity.this, "Password is incorrect", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Username not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Database error", error.toException());
            }
        });
    }
    //going into new activity with shared preference

    private void saveUsernameAndNavigate(String username) {
        // Save username in SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USERNAME_KEY, username);
        editor.apply();

        // Start HomeActivity
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
