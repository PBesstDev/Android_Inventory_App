package com.example.bessmertnyy_3_2_assignment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

//Main screen for navigation for all other screens
public class MainViewActivity extends AppCompatActivity {

    private static final int REQUEST_SMS_PERMISSION = 1001;

    // UI elements for phone number input
    private EditText etSmsNumber;
    private String pendingSmsNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainview);

        // 1. Initialize Buttons and input fields using XML IDs
        Button btnUserProfile = findViewById(R.id.btn_user_profile);
        Button btnExistingInventories = findViewById(R.id.btn_existing_inventories);
        Button btnCreateInventory = findViewById(R.id.btn_create_inventory);
        etSmsNumber = findViewById(R.id.etSmsNumber);
        Button btnSendSms = findViewById(R.id.btnSendSms);

        // 2. Setup Click Listeners for navigation and actions

        // Navigate to User Profile (Placeholder)
        btnUserProfile.setOnClickListener(v -> {
            Toast.makeText(MainViewActivity.this, "Opening User Profile...", Toast.LENGTH_SHORT).show();
            // TODO: Implementation for profile page
        });

        // Navigate to list of inventories
        btnExistingInventories.setOnClickListener(v -> {
            Intent intent = new Intent(MainViewActivity.this, InventoryViewActivity.class);
            startActivity(intent);
        });

        // Create a new inventory (Placeholder)
        btnCreateInventory.setOnClickListener(v -> {
            Toast.makeText(MainViewActivity.this, "Starting New Inventory...", Toast.LENGTH_SHORT).show();
            // TODO: Implementation for creating new inventory containers
        });

        // Handle SMS alert configuration
        btnSendSms.setOnClickListener(v -> sendInventorySms());
    }

    //Validate input and initiate SMS permission request if necessary.
    private void sendInventorySms() {
        String phoneNumber = etSmsNumber.getText().toString().trim();
        if (phoneNumber.isEmpty()) {
            Toast.makeText(this, "Enter a phone number first.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Persist the phone number for automated low-inventory alerts
        getSharedPreferences("AppPrefs", MODE_PRIVATE).edit().putString("sms_number", phoneNumber).apply();

        pendingSmsNumber = phoneNumber;

        // Check if SEND_SMS permission is already granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            sendSmsMessage(phoneNumber);
        } else {
            // Trigger system permission dialog
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, REQUEST_SMS_PERMISSION);
        }
    }

    //Sends confirmation text message using the appropriate API version
    private void sendSmsMessage(String phoneNumber) {
        try {
            SmsManager smsManager;
            smsManager = SmsManager.getDefault();
            // Send the notification
            smsManager.sendTextMessage(phoneNumber, null, "Inventory alert: SMS notifications enabled.", null, null);
            Toast.makeText(this, "SMS notifications enabled.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "SMS could not be sent.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    //Callback for result from requesting permissions.
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_SMS_PERMISSION) {
            // Check if user granted the permission
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendSmsMessage(pendingSmsNumber);
            } else {
                // Inform user that the app will function without SMS notifications
                Toast.makeText(this, "SMS permission denied. App still works without SMS.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}