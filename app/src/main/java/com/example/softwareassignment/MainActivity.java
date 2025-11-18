package com.example.softwareassignment;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnInspection).setOnClickListener(v ->
                startActivity(new Intent(this, InspectionActivity.class))
        );

        findViewById(R.id.btnHistory).setOnClickListener(v ->
                startActivity(new Intent(this, HistoryActivity.class))
        );

        findViewById(R.id.btnUserManual).setOnClickListener(v ->
                startActivity(new Intent(this, UserManualActivity.class))
        );
    }
}
