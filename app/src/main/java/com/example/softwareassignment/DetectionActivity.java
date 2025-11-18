package com.example.softwareassignment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public class DetectionActivity extends AppCompatActivity {

    private ImageView imageViewDetection;
    private ProgressBar progressBar;
    private TextView textResults;
    private Handler handler = new Handler();
    private int progressStatus = 0;
    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detection);

        imageViewDetection = findViewById(R.id.imageViewDetection);
        progressBar = findViewById(R.id.progressBar);
        textResults = findViewById(R.id.textResults);

        imagePath = getIntent().getStringExtra("imagePath");
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            imageViewDetection.setImageBitmap(bitmap);
            startProgressBar();

            new Thread(() -> {
                try {
                    // 🔹 Step 1: Convert image to Base64
                    String base64 = imageToBase64(imagePath);
                    String body = "{ \"image\": \"" + "data:image/png;base64," + base64 + "\" }";

                    // 🔹 Step 2: Send POST request
                    String apiUrl = "http://192.168.0.41:5000//fastDetect";
                    String result = sendPost(apiUrl, body);

                    // 🔹 Step 3: Update UI
                    runOnUiThread(() -> textResults.setText(result));

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> textResults.setText("Error: " + e.getMessage()));
                }
            }).start();

        } else {
            textResults.setText("No image received.");
        }
    }

    private void startProgressBar() {
        progressBar.setProgress(0);
        progressStatus = 0;

        new Thread(() -> {
            while (progressStatus < 100) {
                progressStatus += 1;
                handler.post(() -> progressBar.setProgress(progressStatus));
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            handler.post(this::showDetectionResults);
        }).start();
    }

    private void showDetectionResults() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String deviceInfo = Build.MANUFACTURER + " " + Build.MODEL + " (Android " + Build.VERSION.RELEASE + ")";
        String result = "Impurities detected:%";

        String finalText = "Detection Complete\n\n"
                + "Timestamp: " + timestamp + "\n"
                + "Device: " + deviceInfo + "\n"
                + "Result: " + result;

        textResults.setText(finalText);
    }

    // 🔧 Utility: Convert image to Base64
    public static String imageToBase64(String imagePath) throws Exception {
        File file = new File(imagePath);
        FileInputStream fis = new FileInputStream(file);
        byte[] bytes = new byte[(int) file.length()];
        fis.read(bytes);
        fis.close();
        return Base64.getEncoder().encodeToString(bytes);
    }

    // 🔧 Utility: Send POST request with JSON
    public static String sendPost(String urlStr, String jsonBody) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonBody.getBytes());
            os.flush();
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            response.append(line);
        }
        br.close();

        return response.toString();
    }
}