package com.example.softwareassignment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class InspectionActivity extends AppCompatActivity {

    private ImageView imagePreview;
    private String savedImagePath;

    private final ActivityResultLauncher<Void> takePictureLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), result -> {
                if (result != null) {
                    imagePreview.setImageBitmap(result);

                    String filename = "sample_" + UUID.randomUUID().toString() + ".png";
                    try (FileOutputStream fos = openFileOutput(filename, MODE_PRIVATE)) {
                        result.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        savedImagePath = getFilesDir().getAbsolutePath() + "/" + filename;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    String deviceInfo = Build.MANUFACTURER + " " + Build.MODEL + " (Android " + Build.VERSION.RELEASE + ")";
                    DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
                    boolean success = dbHelper.insertInspection(savedImagePath, System.currentTimeMillis(), "Pending", deviceInfo);
                    Log.d("InspectionActivity", "Insert success: " + success);

                    Intent intent = new Intent(this, DetectionActivity.class);
                    intent.putExtra("imagePath", savedImagePath);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "No image captured", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    takePictureLauncher.launch(null);
                } else {
                    Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection);

        imagePreview = findViewById(R.id.imagePreview);
        Button btnInspection = findViewById(R.id.btnInspection);

        btnInspection.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                takePictureLauncher.launch(null);
            } else {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA);
            }
        });
    }
}