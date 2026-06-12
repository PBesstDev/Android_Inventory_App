package com.example.bessmertnyy_3_2_assignment;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


//Adding new item to a specific inventory.
public class AddItemActivity extends AppCompatActivity {

    // UI input components
    private EditText etItemName;
    private EditText etQuantity;
    private EditText etLocation;
    
    // Database and inventory tracking
    private InventoryDatabaseHelper inventoryDatabaseHelper;
    private String inventoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additem);

        // Initialize DB helper and ensure example data exists
        inventoryDatabaseHelper = new InventoryDatabaseHelper(this);
        inventoryDatabaseHelper.ensureExampleInventoryExists();
        
        // Determine which inventory this item belongs to
        inventoryName = getIntent().getStringExtra("inventory_name");
        if (inventoryName == null || inventoryName.trim().isEmpty()) {
            inventoryName = InventoryDatabaseHelper.EXAMPLE_INVENTORY_NAME;
        }

        // Bind UI components
        etItemName = findViewById(R.id.etItemName);
        etQuantity = findViewById(R.id.etQuantity);
        etLocation = findViewById(R.id.etLocation);

        Button btnSaveItem = findViewById(R.id.btnSaveItem);
        Button btnCancel = findViewById(R.id.btnCancel);

        // Click listeners for saving or canceling
        btnSaveItem.setOnClickListener(v -> saveItem());
        btnCancel.setOnClickListener(v -> finish());
    }


    //Gather input data, save them to database
    private void saveItem() {
        String name = etItemName.getText().toString().trim();
        String quantityText = etQuantity.getText().toString().trim();
        String location = etLocation.getText().toString().trim();

        // Basic validation for empty fields
        if (name.isEmpty() || quantityText.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Fill in all item fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Insert item into the database
        boolean added = inventoryDatabaseHelper.addItem(inventoryName, name, quantityText, location);
        if (added) {
            Toast.makeText(this, "Item saved.", Toast.LENGTH_SHORT).show();
            // Automatically check if an SMS alert should be sent
            checkLowInventory(name, quantityText);
            finish();
        } else {
            Toast.makeText(this, "Item was not saved.", Toast.LENGTH_SHORT).show();
        }
    }

    //Check for low inventory; send text if it is
    private void checkLowInventory(String name, String quantityText) {
        try {
            int qty = Integer.parseInt(quantityText);
            //TODO: change threshold level to a customizable number.

            int threshold = 5;
            if (qty < threshold) {
                // Verify SMS permissions
                if (androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) 
                        == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    
                    // Retrieve saved alert phone number from SharedPreferences
                    String phoneNumber = getSharedPreferences("AppPrefs", MODE_PRIVATE).getString("sms_number", "");
                    if (!phoneNumber.isEmpty()) {
                        android.telephony.SmsManager smsManager;
                        // Handle modern vs legacy SMS manager retrieval
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                            smsManager = this.getSystemService(android.telephony.SmsManager.class);
                        } else {
                            smsManager = android.telephony.SmsManager.getDefault();
                        }
                        // Send the automated notification
                        smsManager.sendTextMessage(phoneNumber, null, "Low inventory alert: " + name + " is down to " + qty, null, null);
                    }
                }
            }
        } catch (NumberFormatException e) {
            // Logically impossible with numeric input filter, but handled for safety
        }
    }
}