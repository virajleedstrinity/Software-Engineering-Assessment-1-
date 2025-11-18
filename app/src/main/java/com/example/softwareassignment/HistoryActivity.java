package com.example.softwareassignment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity {

    private ListView listView;
    private DatabaseHelper dbHelper;
    private Cursor cursor;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        listView = findViewById(R.id.listView);
        dbHelper = DatabaseHelper.getInstance(this); // Use singleton

        ArrayList<String> historyItems = new ArrayList<>();

        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            // ✅ FIX: Query the InspectionRecords table
            cursor = db.rawQuery("SELECT * FROM InspectionRecords ORDER BY timestamp DESC", null);

            if (cursor != null && cursor.moveToFirst()) {
                int pathIndex = cursor.getColumnIndex("imagePath");
                int timeIndex = cursor.getColumnIndex("timestamp");
                int statusIndex = cursor.getColumnIndex("status");
                int deviceIndex = cursor.getColumnIndex("deviceInfo");

                do {
                    String path = pathIndex != -1 ? cursor.getString(pathIndex) : "N/A";
                    String timestampStr = timeIndex != -1 ? cursor.getString(timeIndex) : "0";
                    String status = statusIndex != -1 ? cursor.getString(statusIndex) : "Unknown";
                    String deviceInfo = deviceIndex != -1 ? cursor.getString(deviceIndex) : "Unknown device";

                    long timestamp = Long.parseLong(timestampStr);
                    String timeStr = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
                            .format(new Date(timestamp));

                    historyItems.add("📷 " + timeStr +
                            "\nStatus: " + status +
                            "\nDevice: " + deviceInfo +
                            "\nPath: " + path);
                } while (cursor.moveToNext());
            } else {
                historyItems.add("No inspection records found.");
            }
        } catch (Exception e) {
            Log.e("HistoryActivity", "Error loading history", e);
            Toast.makeText(this, "Error loading history", Toast.LENGTH_SHORT).show();
            historyItems.add("Failed to load inspection history.");
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, historyItems);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (dbHelper != null) {
                dbHelper.close();
            }
            if (listView != null) {
                listView.setAdapter(null);
            }
        } catch (Exception e) {
            Log.e("HistoryActivity", "Error during cleanup", e);
        }
    }
}