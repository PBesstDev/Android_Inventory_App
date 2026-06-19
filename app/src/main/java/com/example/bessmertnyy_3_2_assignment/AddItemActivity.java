package com.example.bessmertnyy_3_2_assignment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class AddItemActivity extends AppCompatActivity {

    private static final String DEFAULT_SMS_ALERT_NUMBER = "5554";

    private EditText etItemName;
    private EditText etQuantity;
    private EditText etLocation;

    private InventoryDatabaseHelper inventoryDatabaseHelper;
    private String inventoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additem);

        inventoryDatabaseHelper = new InventoryDatabaseHelper(this);
        inventoryDatabaseHelper.ensureExampleInventoryExists();

        inventoryName = getIntent().getStringExtra("inventory_name");
        if (inventoryName == null || inventoryName.trim().isEmpty()) {
            inventoryName = InventoryDatabaseHelper.EXAMPLE_INVENTORY_NAME;
        }

        etItemName = findViewById(R.id.etItemName);
        etQuantity = findViewById(R.id.etQuantity);
        etLocation = findViewById(R.id.etLocation);

        Button btnSaveItem = findViewById(R.id.btnSaveItem);
        Button btnCancel = findViewById(R.id.btnCancel);

        btnSaveItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveItem();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void saveItem() {
        String name = etItemName.getText().toString().trim();
        String quantityText = etQuantity.getText().toString().trim();
        String location = etLocation.getText().toString().trim();

        if (name.isEmpty() || quantityText.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Fill in all item fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean added = inventoryDatabaseHelper.addItem(inventoryName, name, quantityText, location);
        if (added) {
            Toast.makeText(this, "Item saved.", Toast.LENGTH_SHORT).show();
            checkLowInventory(name, quantityText);
            finish();
        } else {
            Toast.makeText(this, "Item was not saved.", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkLowInventory(String name, String quantityText) {
        try {
            int qty = Integer.parseInt(quantityText);

            int threshold = 5;
            if (qty < threshold) {
                if (androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) 
                        == android.content.pm.PackageManager.PERMISSION_GRANTED) {

                    String activeUsername = getSharedPreferences("AppPrefs", MODE_PRIVATE)
                            .getString("logged_in_username", "");
                    String phoneNumber = new UserDatabaseHelper(this).getUserPhoneNumber(activeUsername);

                    if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
                        phoneNumber = getSharedPreferences("AppPrefs", MODE_PRIVATE).getString("sms_number", "");
                    }

                    if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
                        phoneNumber = DEFAULT_SMS_ALERT_NUMBER;
                        getSharedPreferences("AppPrefs", MODE_PRIVATE)
                                .edit()
                                .putString("sms_number", phoneNumber)
                                .apply();
                    }

                    if (!phoneNumber.isEmpty()) {
                        android.telephony.SmsManager smsManager;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                            smsManager = this.getSystemService(android.telephony.SmsManager.class);
                            if (smsManager == null) {
                                smsManager = android.telephony.SmsManager.getDefault();
                            }
                        } else {
                            smsManager = android.telephony.SmsManager.getDefault();
                        }
                        smsManager.sendTextMessage(phoneNumber, null, "Low inventory alert: " + name + " is down to " + qty, null, null);
                    }
                }
            }
        } catch (NumberFormatException e) {
        }
    }
}
