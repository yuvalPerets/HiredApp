package com.example.hiredapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private LinearLayout signUpForm;
    private LinearLayout signInForm;
    private Button signUpButton;
    private Button signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signUpForm = findViewById(R.id.signUpForm);
        signInForm = findViewById(R.id.signInForm);
        signUpButton = findViewById(R.id.signUpButton);
        signInButton = findViewById(R.id.signInButton);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show Sign Up form and hide Sign In form
                signUpForm.setVisibility(View.VISIBLE);
                signInForm.setVisibility(View.GONE);
                signUpButton.setVisibility(View.GONE); // Hide Sign Up button
                signInButton.setVisibility(View.VISIBLE); // Show Sign In button
                // Set marginTop to 200dp for the Sign In button
                RelativeLayout.LayoutParams signInParams = (RelativeLayout.LayoutParams) signInButton.getLayoutParams();
                signInParams.topMargin = (int) getResources().getDimension(R.dimen.margin_top_300dp);
                signInButton.setLayoutParams(signInParams);
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show Sign In form and hide Sign Up form
                signInForm.setVisibility(View.VISIBLE);
                signUpForm.setVisibility(View.GONE);
                signInButton.setVisibility(View.GONE); // Hide Sign In button
                signUpButton.setVisibility(View.VISIBLE); // Show Sign Up button
            }
        });

        // Example: Implement sign-up or sign-in logic when submit buttons are clicked
        Button signUpSubmitButton = findViewById(R.id.signupSubmitButton);
        signUpSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle sign-up submission
                // Example: Validate input fields and process sign-up logic
            }
        });

        Button signInSubmitButton = findViewById(R.id.signinSubmitButton);
        signInSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle sign-in submission
                // Example: Validate input fields and process sign-in logic
            }
        });
    }
}
