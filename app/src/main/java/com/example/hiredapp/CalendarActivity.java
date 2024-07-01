package com.example.hiredapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Map;

public class CalendarActivity extends AppCompatActivity {
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String USERNAME_KEY = "username";
    private static final int REQUEST_SMS_PERMISSION = 123;

    private Button btnBackToMain;
    private Button btnViewNote;
    private TextView usernameText;

    private String username ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        btnBackToMain = findViewById(R.id.mainButton);
        btnViewNote = findViewById(R.id.viewNoteButton);
        usernameText = findViewById(R.id.usernameText);

        // Retrieve the username from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        username = sharedPreferences.getString(USERNAME_KEY, "");
        usernameText.setText(username);

        // Handle back to main button click
        btnBackToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUsernameAndNavigate(username, HomeActivity.class);
            }
        });

        // Handle view note button click
        btnViewNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUsernameAndNavigate(username, NotesViewActivity.class);
            }
        });

        // Set an example CalendarView listener (adjust as needed)
        CalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                showAddMeetingDialog(year, month, dayOfMonth);
            }
        });

        // Update calendar view initially
        updateCalendarView();
    }

    private void showAddMeetingDialog(int year, int month, int dayOfMonth) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_meeting, null);
        builder.setView(dialogView);

        EditText companyName = dialogView.findViewById(R.id.companyName);
        EditText phoneNumber = dialogView.findViewById(R.id.phoneNumber);
        Button sendSmsButton = dialogView.findViewById(R.id.sendSmsButton);
        CheckBox sendSmsCheckbox = dialogView.findViewById(R.id.sendSmsCheckbox); // Add CheckBox for sending SMS

        AlertDialog dialog = builder.create();

        sendSmsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String company = companyName.getText().toString();
                String phone = phoneNumber.getText().toString();
                String message = "Meeting with " + company + " scheduled on " + dayOfMonth + "/" + (month + 1) + "/" + year;

                if (!company.isEmpty() && !phone.isEmpty()) {
                    // Check if SMS checkbox is checked
                    boolean sendSmsChecked = sendSmsCheckbox.isChecked();

                    if (sendSmsChecked) {
                        // Request SMS permission if not granted
                        if (ContextCompat.checkSelfPermission(CalendarActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(CalendarActivity.this, new String[]{Manifest.permission.SEND_SMS}, REQUEST_SMS_PERMISSION);
                        } else {
                            sendSms(phone, message);
                        }
                    }

                    // Save meeting details to Firebase
                    saveMeetingToFirebase(username, year, month, dayOfMonth, company, phone);

                    // Save meeting locally (SharedPreferences)
                    saveMeeting(year, month, dayOfMonth, company, phone);

                    updateCalendarView(); // Update calendar view after saving meeting
                    dialog.dismiss();
                } else {
                    Toast.makeText(CalendarActivity.this, "Please enter all details", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }

    private void updateCalendarView() {
        CalendarView calendarView = findViewById(R.id.calendarView);
        // Fetch meeting details from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        Map<String, ?> allEntries = sharedPreferences.getAll();

        // Iterate through saved entries to mark meeting dates on the calendar
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String dateKey = entry.getKey();
            String[] dateParts = dateKey.split("-");
            if (dateParts.length == 3) {
                int year = Integer.parseInt(dateParts[0]);
                int month = Integer.parseInt(dateParts[1]);
                int dayOfMonth = Integer.parseInt(dateParts[2]);

                // Mark this date on the calendar (customize as needed)
                calendarView.setDate(getDateInMillis(year, month, dayOfMonth), true, true);
            }
        }

        calendarView.invalidate(); // Forces the calendar to redraw itself
    }

    private long getDateInMillis(int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        return calendar.getTimeInMillis();
    }

    private void sendSms(String phoneNumber, String message) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
        Toast.makeText(this, "SMS sent.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_SMS_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted. Please try again.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied to send SMS.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveMeeting(int year, int month, int dayOfMonth, String company, String phone) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String dateKey = year + "-" + month + "-" + dayOfMonth;
        String meetingDetails = company + ";" + phone;
        editor.putString(dateKey, meetingDetails);
        editor.apply();
    }

    private void saveMeetingToFirebase(String username, int year, int month, int dayOfMonth, String company, String phone) {
        // Format the date as a string
        String date = dayOfMonth + "-" + (month + 1) + "-" + year;

        // Assuming you have Firebase initialized and a reference to your Firebase database
        DatabaseReference meetingsRef = FirebaseDatabase.getInstance().getReference()
                .child("users").child(username).child("meetings");

        // Generate a unique key for the meeting
        String meetingKey = meetingsRef.push().getKey();

        // Create a meeting object with company name, date, and phone number
        Meeting meeting = new Meeting(company, date, phone);

        // Save the meeting to Firebase
        assert meetingKey != null;
        meetingsRef.child(meetingKey).setValue(meeting);
    }

    private void saveUsernameAndNavigate(String username, Class<?> destinationActivity) {
        // Save username in SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USERNAME_KEY, username);
        editor.apply();

        // Start the destination activity
        Intent intent = new Intent(CalendarActivity.this, destinationActivity);
        startActivity(intent);
        finish();
    }
    public class Meeting {
        private String companyName;
        private String date; // Combine day, month, year into a single string
        private String phoneNumber;

        // Required default constructor for Firebase
        public Meeting() {
            // Default constructor required for calls to DataSnapshot.getValue(Meeting.class)
        }

        public Meeting(String companyName, String date, String phoneNumber) {
            this.companyName = companyName;
            this.date = date;
            this.phoneNumber = phoneNumber;
        }

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }
    }
}
