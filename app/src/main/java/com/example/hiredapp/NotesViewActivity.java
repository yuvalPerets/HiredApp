package com.example.hiredapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class NotesViewActivity extends AppCompatActivity {
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String USERNAME_KEY = "username";

    // UI elements
    private Button btnBackToMain;
    private ListView listViewNotes;
    private TextView usernameTextView;

    // List to hold notes data
    private List<Map<String, Object>> notesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notes_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI elements
        btnBackToMain = findViewById(R.id.mainButton);
        listViewNotes = findViewById(R.id.listViewNotes);
        usernameTextView = findViewById(R.id.usernameTextView);

        // Retrieve the username from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        String username = sharedPreferences.getString(USERNAME_KEY, "");

        // Display the username
        usernameTextView.setText(username);

        // Initialize the notes list
        notesList = new ArrayList<>();

        // Fetch notes from Firebase
        fetchNotes(username);

        // Handle back to main button click
        btnBackToMain.setOnClickListener(v -> saveUsernameAndNavigate(username));
    }

    private void fetchNotes(String username) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(username).child("notes");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                notesList.clear();
                for (DataSnapshot noteSnapshot : dataSnapshot.getChildren()) {
                    // Extract note details from dataSnapshot
                    String date = noteSnapshot.child("date").getValue(String.class);
                    String noteText = noteSnapshot.child("note").getValue(String.class);
                    String companyName = noteSnapshot.child("companyName").getValue(String.class);

                    // Format date if needed
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        Date formattedDate = dateFormat.parse(date);
                        date = DateFormat.getDateInstance(DateFormat.LONG).format(formattedDate); // Format as per your requirement
                    } catch (ParseException e) {
                        Log.e("NotesViewActivity", "Date parsing error: " + e.getMessage());
                    }

                    // Create map for note details
                    Map<String, Object> note = new HashMap<>();
                    note.put("date", date);
                    note.put("note", noteText);
                    note.put("companyName", companyName);

                    // Add note to notesList
                    notesList.add(note);

                    Log.d("NotesViewActivity", "Note fetched: " + note);
                }
                updateListView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
                Log.e("NotesViewActivity", "Error fetching notes", databaseError.toException());
            }
        });
    }


    private void updateListView() {
        String[] from = {"date", "note", "companyName"};
        int[] to = {android.R.id.text1, android.R.id.text2};

        SimpleAdapter adapter = new SimpleAdapter(this, notesList, android.R.layout.simple_list_item_2, from, to) {
            @SuppressLint("SetTextI18n")
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView text1 = view.findViewById(android.R.id.text1);
                TextView text2 = view.findViewById(android.R.id.text2);

                // Set text for date, note, and companyName
                text1.setText("Date: " + Objects.requireNonNull(notesList.get(position).get("date")).toString());
                text2.setText("Company Name: " + Objects.requireNonNull(notesList.get(position).get("companyName")).toString() + "\n" + "Note: " + Objects.requireNonNull(notesList.get(position).get("note")).toString());

                return view;
            }
        };

        listViewNotes.setAdapter(adapter);
    }


    private void saveUsernameAndNavigate(String username) {
        // Save username in SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USERNAME_KEY, username);
        editor.apply();

        // Start HomeActivity
        Intent intent = new Intent(NotesViewActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}