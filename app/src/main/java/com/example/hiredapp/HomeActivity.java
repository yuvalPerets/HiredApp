package com.example.hiredapp;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    private TextView welcomeTextView;
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String USERNAME_KEY = "username";

    private static final String[] QUOTES = {
            "Choose a job you love, and you will never have to work a day in your life.",
            "Opportunities don't happen, you create them.",
            "Success doesn’t come to you, you go to it.",
            "The future depends on what you do today.",
            "Act as if what you do makes a difference. It does."
    };

    private int quoteIndex = 0;
    private TextView quoteTextView;
    private Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        welcomeTextView = findViewById(R.id.welcomeTextView);

        // Retrieve the username from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        String username = sharedPreferences.getString(USERNAME_KEY, "");

        // Log retrieved username
        Log.d(TAG, "Retrieved username: " + username);

        // Display the username
        welcomeTextView.setText(username);


        CardView button1 = findViewById(R.id.button1);
        CardView button2 = findViewById(R.id.button2);
        CardView button3 = findViewById(R.id.button3);
        CardView button4 = findViewById(R.id.button4);
        quoteTextView = findViewById(R.id.quoteTextView);


        // Update quotes periodically
        updateQuote();

        // Handle button clicks to navigate to new activities
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUsernameAndNavigateNotes(username);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(USERNAME_KEY, username);
                editor.apply();

                // Start HomeActivity
                Intent intent = new Intent(HomeActivity.this, NotesViewActivity.class);
                startActivity(intent);
                finish();
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(USERNAME_KEY, username);
                editor.apply();

                // Start HomeActivity
                Intent intent = new Intent(HomeActivity.this, TipsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUsernameAndNavigateCalendar(username);
            }
        });
    }
    private void updateQuote() {
        quoteTextView.setText(QUOTES[quoteIndex]);
        quoteIndex = (quoteIndex + 1) % QUOTES.length;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateQuote();
            }
        }, 5000); // Update quote every 5 seconds
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null); // Remove all callbacks when activity is destroyed
    }
    private void saveUsernameAndNavigateNotes(String username ) {
        // Save username in SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USERNAME_KEY, username);
        editor.apply();

        // Start HomeActivity
        Intent intent = new Intent(HomeActivity.this, notes.class);
        startActivity(intent);
        finish();
    }
    private void saveUsernameAndNavigateCalendar(String username ) {
        // Save username in SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USERNAME_KEY, username);
        editor.apply();

        // Start HomeActivity
        Intent intent = new Intent(HomeActivity.this, CalendarActivity.class);
        startActivity(intent);
        finish();
    }
}