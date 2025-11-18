package com.example.softwareassignment;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "my.db"; // Use lecturer's DB name
    private static final int DB_VERSION = 1;
    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Inspection table
        db.execSQL("CREATE TABLE IF NOT EXISTS InspectionRecords (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "imagePath TEXT, " +
                "timestamp TEXT, " +
                "status TEXT, " +
                "deviceInfo TEXT)");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Future upgrade logic
    }

    // Insert inspection record
    public boolean insertInspection(String imagePath, long timestamp, String status, String deviceInfo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("imagePath", imagePath);
        long result = db.insert("InspectionRecords", null, values);
        return result != -1;
    }


    // POST: Send inspection data to API
    public String sendInspectionToApi(String apiUrl, String imagePath, long timestamp, String status, String deviceInfo) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(apiUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            // JSON payload
            String jsonInputString = "{"
                    + "\"imagePath\":\"" + imagePath + "\","
                    + "\"timestamp\":\"" + timestamp + "\","
                    + "\"status\":\"" + status + "\","
                    + "\"deviceInfo\":\"" + deviceInfo + "\""
                    + "}";

            // Send request
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Read response
            int code = connection.getResponseCode();
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), "utf-8"));
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }

            return "Response Code: " + code + ", Body: " + response.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    // GET: Fetch inspection data from API
    public String fetchInspectionDataFromApi(String apiUrl) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(apiUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            // Read response
            int code = connection.getResponseCode();
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), "utf-8"));
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }

            return "Response Code: " + code + ", Body: " + response.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    // Optional: Encode image file to Base64
    public static String encodeImageToBase64(String imagePath) {
        try {
            File imageFile = new File(imagePath);
            FileInputStream fis = new FileInputStream(imageFile);
            byte[] imageBytes = new byte[(int) imageFile.length()];
            fis.read(imageBytes);
            fis.close();

            return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}