package com.example.bessmertnyy_3_2_assignment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


//Adding new item to a specific inventory.
public class AddItemActivity extends AppCompatActivity {

    //Default phone number. Currently set up for Android emulator testing.
    private static final String DEFAULT_SMS_ALERT_NUMBER = "5554";

    // UI input components
    private EditText etItemName;
    private EditText etQuantity;
    private EditText etLocation;

    // Database and inventory tracking, passed from previous screen
    private InventoryDatabaseHelper myInventoryDBHelper;
    private String inventoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additem);

        // Initialize DB helper and ensure example data exists
        myInventoryDBHelper = new InventoryDatabaseHelper(this);
        myInventoryDBHelper.ensureExampleInventoryExists();

        inventoryName = getIntent().getStringExtra("inventory_name");
        if (inventoryName == null || inventoryName.trim().isEmpty()) {
            inventoryName = myInventoryDBHelper.EXAMPLE_INVENTORY_NAME;
        }

        // Bind UI components
        etItemName = findViewById(R.id.etItemName);
        etQuantity = findViewById(R.id.etQuantity);
        etLocation = findViewById(R.id.etLocation);

        // Click listeners for saving or canceling
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


    // Read form values, validate them, saves item, then confirm if low stock.
    private void saveItem() {
        String name = etItemName.getText().toString().trim();
        String quantityText = etQuantity.getText().toString().trim();
        String location = etLocation.getText().toString().trim();

        //check for empty fields
        if (name.isEmpty() || quantityText.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Fill in all item fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        //Insert item into DB
        boolean added = myInventoryDBHelper.addItem(inventoryName, name, quantityText, location);
        if (added) {
            Toast.makeText(this, "Item saved.", Toast.LENGTH_SHORT).show();
            checkLowInventory(name, quantityText);
            finish();
        } else {
            Toast.makeText(this, "Item was not saved.", Toast.LENGTH_SHORT).show();
        }
    }

    // Sends low inventory SMS if quantity is below threshold and permission is granted.
    private void checkLowInventory(String name, String quantityText) {
        try {
            int qty = Integer.parseInt(quantityText);

            //set minimum quantity threshold for low inventory alert
            //TODO: consider making this user-configurable in the future
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

                    // Send SMS alert about low inventory
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
            //Dont think we'll ever reach this exception, but who knows?
            Toast.makeText(this, "Invalid quantity format for low inventory check.", Toast.LENGTH_SHORT).show();
        }
    }
}
