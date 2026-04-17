package com.example.miniproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;
    EditText etEventName;
    Button btnSaveEvent, btnUpdateEvent, btnDeleteEvent, btnLogout;
    ListView listEvents;
    ArrayList<String> eventList;
    ArrayList<Integer> eventIdList; // To store IDs hiddenly
    ArrayAdapter<String> adapter;
    int selectedEventId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DatabaseHelper(this);
        etEventName = findViewById(R.id.etEventName);
        btnSaveEvent = findViewById(R.id.btnSaveEvent);
        btnUpdateEvent = findViewById(R.id.btnUpdateEvent);
        btnDeleteEvent = findViewById(R.id.btnDeleteEvent);
        btnLogout = findViewById(R.id.btnLogout);
        listEvents = findViewById(R.id.listEvents);

        eventList = new ArrayList<>();
        eventIdList = new ArrayList<>();

        loadEvents();

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.remove("current_user");
                editor.apply();

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnSaveEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = etEventName.getText().toString();
                dbHelper.insertEvent(name);
                etEventName.setText("");
                loadEvents();
            }
        });

        btnUpdateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedEventId != -1) {
                    String name = etEventName.getText().toString();
                    dbHelper.updateEvent(selectedEventId, name);
                    etEventName.setText("");
                    selectedEventId = -1;
                    loadEvents();
                }
            }
        });

        btnDeleteEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedEventId != -1) {
                    dbHelper.deleteEvent(selectedEventId);
                    etEventName.setText("");
                    selectedEventId = -1;
                    loadEvents();
                }
            }
        });

        listEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedEventId = eventIdList.get(position);
                String eventName = eventList.get(position);
                etEventName.setText(eventName);

                Cursor pCursor = dbHelper.getParticipantsCount(selectedEventId);
                int count = pCursor.getCount();
                pCursor.close();

                Toast.makeText(MainActivity.this, count + " student(s) registered.", Toast.LENGTH_SHORT).show();
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
        adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, eventList);
        listEvents.setAdapter(adapter);
    }
}