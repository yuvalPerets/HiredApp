package com.example.hiredapp;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

public class TipsActivity extends AppCompatActivity {
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String USERNAME_KEY = "username";
    private VideoView videoView;
    private ImageView animationView;

    private final String[] tips = {
            "Better LinkedIn",
            "How to Hunt for a Job",
            "Make Yourself a Better Candidate"
    };

    private String[] videoUrls ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);

        TextView titleTextView = findViewById(R.id.titleTextView);
        TextView descriptionTextView = findViewById(R.id.descriptionTextView);
        Spinner spinnerTips = findViewById(R.id.spinnerTips);
        videoView =  (VideoView)findViewById(R.id.videoView);
        Button btnBackToMain = findViewById(R.id.mainButton);
        animationView = findViewById(R.id.animationView);

        // Initialize videoUrls here
        videoUrls = new String[] {
                "android.resource://" + getPackageName() + "/" + R.raw.linkdin,
                "android.resource://" + getPackageName() + "/" + R.raw.jobhunt,
                "android.resource://" + getPackageName() + "/" + R.raw.bestcandidate
        };

        // Retrieve the username from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        String username = sharedPreferences.getString(USERNAME_KEY, "");

        // Set up Spinner with tips
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tips);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTips.setAdapter(adapter);

        // Set spinner item click listener
        spinnerTips.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                playVideo(videoUrls[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Handle back to main button click
        btnBackToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUsernameAndNavigate(username);
            }
        });



        // Optionally set the title and description
        titleTextView.setText("Page for Tips");
        descriptionTextView.setText("Here you can find different types of tips to get you hired faster and smarter.");

        // Set up animation
        setupAnimation();
    }

    private void saveUsernameAndNavigate(String username) {
        // Save username in SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USERNAME_KEY, username);
        editor.apply();

        // Start HomeActivity
        Intent intent = new Intent(TipsActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void playVideo(String videoUrl) {
        Uri uri = Uri.parse(videoUrl);
        videoView.setVideoURI(uri);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.start();
            }
        });
        // Set up media controller
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
    }

    private void setupAnimation() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(animationView, "translationX", -1000f, 1000f);
        animator.setDuration(10000);
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.setRepeatMode(ObjectAnimator.RESTART);
        animator.start();
    }
}
