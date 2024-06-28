package com.example.hiredapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CalendarActivity extends AppCompatActivity {
    private static final String SHARED_PREFS = "sharedPrefs";

    private static final String USERNAME_KEY = "username";

    private  Button btnBackToMain ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calendar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnBackToMain = findViewById(R.id.mainButton);
        // Retrieve the username from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        String username = sharedPreferences.getString(USERNAME_KEY, "");
        // Handle back to main button click
        btnBackToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUsernameAndNavigate(username);
            }
        });
    }

    private void saveUsernameAndNavigate(String username) {
        // Save username in SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USERNAME_KEY, username);
        editor.apply();

        // Start HomeActivity
        Intent intent = new Intent(CalendarActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}