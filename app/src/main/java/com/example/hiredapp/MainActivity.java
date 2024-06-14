package com.example.hiredapp;

import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {

    private LinearLayout signUpForm;
    private LinearLayout signInForm;
    private Button signUpButton;
    private Button signInButton;



    private String username ;
    private String password ;

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
                EditText editusername =  findViewById(R.id.signupUsername) ;
                username = editusername.getText().toString();
                EditText editpassword =  findViewById(R.id.signupPassword) ;
                password = editpassword.getText().toString();


                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Username and Password cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }


                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference usersRef = database.getReference();
                usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean usernameExists = false;

                        // Iterate through all the children in the database
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            String existingUsername = userSnapshot.getKey();
                            if (existingUsername != null && existingUsername.equals(username)) {
                                usernameExists = true;
                                break;
                            }
                        }
                        if (usernameExists) {
                            // Username already exists
                            Toast.makeText(MainActivity.this, "Username already exists", Toast.LENGTH_SHORT).show();
                        } else {
                            // Username does not exist, proceed with registration
                            usersRef.child(username).setValue(password);
                            Toast.makeText(MainActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MainActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });


                //DatabaseReference myRef = database.getReference(username);

                //myRef.setValue(password);

            }
        });

        Button signInSubmitButton = findViewById(R.id.signinSubmitButton);
        signInSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle sign-in submission
                // Example: Validate input fields and process sign-in logic
                // Write a message to the database
                EditText editusername =  findViewById(R.id.signinUsername) ;
                username = editusername.getText().toString();
                EditText editpassword =  findViewById(R.id.signinPassword) ;
                password = editpassword.getText().toString();
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Username and Password cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference usersRef = database.getReference();
                usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean usernameExists = false;

                        // Iterate through all the children in the database
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            String existingUsername = userSnapshot.getKey();
                            if (existingUsername != null && existingUsername.equals(username)) {
                                Toast.makeText(MainActivity.this,"found user",Toast.LENGTH_SHORT).show();
                                usernameExists =  true ;
                                DatabaseReference UsernameFound = database.getReference(username);
                                UsernameFound.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String value = snapshot.getValue(String.class);
                                        if (value.equals(password)){
                                            Toast.makeText(MainActivity.this,"login succes",Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            Toast.makeText(MainActivity.this,"password not correct",Toast.LENGTH_SHORT).show();
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(getApplicationContext(), "Failed to read value." + error.toException(), Toast.LENGTH_SHORT).show();

                                    }
                                });
                                break;
                            }
                        }
                        if (usernameExists) {
                            // Username already exists
                            //Toast.makeText(MainActivity.this, "Username already exists", Toast.LENGTH_SHORT).show();
                        } else {

                            Toast.makeText(MainActivity.this, "Couldn't fine username", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MainActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });


            }
        });
    }
}
