package com.example.miniproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.ArrayList;

public class StudentActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;
    TextView tvWelcome;
    Button btnLogout;
    ListView listStudentEvents;
    ArrayList<String> eventList;
    ArrayList<Integer> eventIdList; // To store IDs hiddenly
    ArrayAdapter<String> adapter;
    String currentStudentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DatabaseHelper(this);
        tvWelcome = findViewById(R.id.tvWelcome);
        btnLogout = findViewById(R.id.btnLogout);
        listStudentEvents = findViewById(R.id.listStudentEvents);
        eventList = new ArrayList<>();
        eventIdList = new ArrayList<>();

        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        currentStudentName = prefs.getString("current_user", "Unknown Student");
        tvWelcome.setText("Welcome, " + currentStudentName + "!");

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.remove("current_user");
                editor.apply();

                Intent intent = new Intent(StudentActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        loadEvents();

        listStudentEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int eventId = eventIdList.get(position);
                String eventName = eventList.get(position);

                if (dbHelper.isAlreadyRegistered(eventId, currentStudentName)) {
                    Toast.makeText(StudentActivity.this, "Already registered!", Toast.LENGTH_SHORT).show();
                } else {
                    dbHelper.insertParticipant(eventId, currentStudentName);
                    Toast.makeText(StudentActivity.this, "Registered for: " + eventName, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void loadEvents() {
        eventList.clear();
        eventIdList.clear();
        Cursor cursor = dbHelper.getAllEvents();
        if (cursor.moveToFirst()) {
            do {
                eventIdList.add(cursor.getInt(0)); // Hidden ID
                eventList.add(cursor.getString(1)); // Visible Name
            } while (cursor.moveToNext());
        }
        cursor.close();
        adapter = new ArrayAdapter<>(StudentActivity.this, android.R.layout.simple_list_item_1, eventList);
        listStudentEvents.setAdapter(adapter);
    }
}