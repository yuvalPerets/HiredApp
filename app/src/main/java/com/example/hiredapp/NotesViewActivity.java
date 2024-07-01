package com.example.hiredapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotesViewActivity extends AppCompatActivity {
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String USERNAME_KEY = "username";
    private Button btnBackToMain;
    private ListView listViewNotes;
    private List<Map<String, String>> notesList;
    private TextView usernameTextView;

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
        btnBackToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUsernameAndNavigate(username);
            }
        });
    }

    private void fetchNotes(String username) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(username).child("notes");
        databaseReference.addValueEventListener(new ValueEventListener() {
            /** @noinspection unchecked*/
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                notesList.clear();
                for (DataSnapshot noteSnapshot : dataSnapshot.getChildren()) {
                    Map<String, String> note = (Map<String, String>) noteSnapshot.getValue();
                    notesList.add(note);
                    Log.d("NotesViewActivity", "Note fetched: " + note);
                }
                updateListView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }

    private void updateListView() {
        String[] from = {"date", "note"};
        int[] to = {android.R.id.text1, android.R.id.text2};

        SimpleAdapter adapter = new SimpleAdapter(this, notesList, android.R.layout.simple_list_item_2, from, to);
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
