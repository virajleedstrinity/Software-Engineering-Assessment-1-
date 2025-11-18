package com.example.softwareassignment;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class UserManualActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_manual);

        TextView manualText = findViewById(R.id.manualText);
        manualText.setText(getUserManualContent());
    }

    private String getUserManualContent() {
        return " Android Inspection App\n"
                + "Developer: Viraj\n"
                + "University: Leeds Trinity University\n"
                + "Course: BSc Computer Science\n"
                + "Version: 1.0\n"
                + "Date: October 2025\n\n"

                + "🔹 1. Introduction\n"
                + "This app enables users to capture sample images, simulate impurity detection, and store inspection records locally.\n\n"

                + "🔹 2. System Requirements\n"
                + "- Android 8.0 or higher\n"
                + "- Camera permission enabled\n"
                + "- Minimum 50MB of free storage\n\n"

                + "🔹 3. Installation Instructions\n"
                + "1. Download the APK file\n"
                + "2. Install and grant camera permission\n"
                + "3. Launch the app from your home screen\n\n"

                + "🔹 4. App Overview\n"
                + "- Inspection Screen: Capture and preview image\n"
                + "- Detection Screen: Simulate impurity detection\n"
                + "- History Screen: View stored inspection records\n\n"

                + "🔹 5. Using the App\n"
                + "• Tap 'Capture Image' to start\n"
                + "• Camera activates automatically\n"
                + "• Take a photo and preview it\n"
                + "• Detection screen shows progress bar and results\n\n"

                + "Detection Results:\n"
                + "- Timestamp\n"
                + "- Device Info\n"
                + "- Impurity Level\n\n"

                + "🔹 6. Troubleshooting\n"
                + "- Camera not opening: Check permissions\n"
                + "- Image not saving: Check storage\n"
                + "- Detection screen not loading: Restart app\n"
                + "- History not showing: Confirm detection completed\n\n"

                + "🔹 7. Compliance Checklist\n"
                + "- Camera activates on button click\n"
                + "- Detection screen with progress bar\n"
                + "- Results include timestamp, device info, impurity level\n"
                + "- Records saved to SQLite database\n"
                + "- UI is modular and meets assignment spec\n";
    }
}