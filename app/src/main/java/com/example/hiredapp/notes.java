package com.example.hiredapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class notes extends AppCompatActivity {

    private TextView tvUsername;
    private EditText etDate;
    private EditText etCompanyName;
    private EditText etNote;
    private Button btnSaveNote;
    private Button btnBackToMain;
    private SharedPreferences sharedPreferences;
    private static final String SHARED_PREFS = "sharedPrefs";

    private static final String USERNAME_KEY = "username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        tvUsername = findViewById(R.id.tvUsername);
        etDate = findViewById(R.id.etDate);
        etCompanyName = findViewById(R.id.etCompanyName);
        etNote = findViewById(R.id.etNote);
        btnSaveNote = findViewById(R.id.btnSaveNote);
        btnBackToMain = findViewById(R.id.btnBackToMain);

        // Retrieve the username from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        String username = sharedPreferences.getString(USERNAME_KEY, "");
        // Set username from SharedPreferences
        tvUsername.setText(username);

        // Set date picker
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        // Handle save note button click
        btnSaveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote(username);
            }
        });

        // Handle back to main button click
        btnBackToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUsernameAndNavigate(username);
            }
        });
    }

    private void showDatePickerDialog() {
        // Hide the keyboard programmatically
        etDate.clearFocus();  // Clear focus from EditText to prevent keyboard from showing

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, month, dayOfMonth);
                        String formattedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.getTime());
                        etDate.setText(formattedDate);
                    }
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void saveNote(String username) {
        String date = etDate.getText().toString();
        String companyName = etCompanyName.getText().toString();
        String note = etNote.getText().toString();

        if (date.isEmpty() || companyName.isEmpty() || note.isEmpty()) {
            Toast.makeText(this, "Please enter a date, company name, and a note", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get reference to Firebase database
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        // Create a unique key for the note
        String noteId = usersRef.push().getKey();

        // Create a Note object
        Note newNote = new Note(date, companyName, note);

        // Save the note under the user's node in Firebase
        usersRef.child(username).child("notes").child(noteId).setValue(newNote);

        Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show();
    }

    private void saveUsernameAndNavigate(String username) {
        // Save username in SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USERNAME_KEY, username);
        editor.apply();

        // Start HomeActivity
        Intent intent = new Intent(notes.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    // POJO class representing a Note
    public static class Note {
        public String date;
        public String companyName;
        public String note;

        public Note() {
            // Default constructor required for calls to DataSnapshot.getValue(Note.class)
        }

        public Note(String date, String companyName, String note) {
            this.date = date;
            this.companyName = companyName;
            this.note = note;
        }
    }
}
