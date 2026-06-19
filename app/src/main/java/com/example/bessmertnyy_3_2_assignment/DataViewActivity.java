package com.example.bessmertnyy_3_2_assignment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;


public class DataViewActivity extends AppCompatActivity {

    private static final String DEFAULT_SMS_ALERT_NUMBER = "5554";

    private RecyclerView recyclerView;
    private FloatingActionButton fabAddData;
    private InventoryAdapter adapter;
    private List<InventoryItem> inventoryList;

    private InventoryDatabaseHelper inventoryDatabaseHelper;
    private String selectedInventoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dataview);

        inventoryDatabaseHelper = new InventoryDatabaseHelper(this);
        inventoryDatabaseHelper.ensureExampleInventoryExists();

        selectedInventoryName = getIntent().getStringExtra("inventory_name");
        if (selectedInventoryName == null || selectedInventoryName.trim().isEmpty()) {
            selectedInventoryName = InventoryDatabaseHelper.EXAMPLE_INVENTORY_NAME;
        }

        recyclerView = findViewById(R.id.recyclerViewData);
        fabAddData = findViewById(R.id.fabAddData);

        fabAddData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddItemScreen();
            }
        });

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        inventoryList = new ArrayList<>();

        adapter = new InventoryAdapter(
                inventoryList,
                new InventoryAdapter.OnItemEditListener() {
                    @Override
                    public void onEdit(InventoryItem item) {
                        showEditDialog(item);
                    }
                },
                new InventoryAdapter.OnItemDeleteListener() {
                    @Override
                    public void onDelete(InventoryItem item) {
                        deleteInventoryItem(item);
                    }
                }
        );
        recyclerView.setAdapter(adapter);

        refreshInventory();
    }

    private void openAddItemScreen() {
        Intent intent = new Intent(DataViewActivity.this, AddItemActivity.class);
        intent.putExtra("inventory_name", selectedInventoryName);
        startActivity(intent);
    }

    private void deleteInventoryItem(InventoryItem item) {
        boolean deleted = inventoryDatabaseHelper.deleteItem(item.getId());
        if (deleted) {
            refreshInventory();
            Toast.makeText(DataViewActivity.this, R.string.item_deleted, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshInventory();
    }

    private void refreshInventory() {
        inventoryList.clear();
        inventoryList.addAll(inventoryDatabaseHelper.getAllItems(selectedInventoryName));
        adapter.notifyDataSetChanged();
    }

    private void showEditDialog(InventoryItem item) {
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        container.setPadding(pad, pad, pad, pad);

        EditText etName = new EditText(this);
        etName.setHint(getString(R.string.additem_item_name));
        etName.setText(item.getName());

        EditText etQuantity = new EditText(this);
        etQuantity.setHint(getString(R.string.additem_item_qty));
        etQuantity.setInputType(InputType.TYPE_CLASS_NUMBER);
        etQuantity.setText(item.getQuantity());

        EditText etLocation = new EditText(this);
        etLocation.setHint(getString(R.string.additem_item_loc));
        etLocation.setText(item.getLocation());

        container.addView(etName);
        container.addView(etQuantity);
        container.addView(etLocation);

        new AlertDialog.Builder(this)
                .setTitle(R.string.edit_item_title)
                .setView(container)
                .setPositiveButton(R.string.edit_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveEditedItem(item, etName, etQuantity, etLocation);
                    }
                })
                .setNegativeButton(R.string.additem_btn_cancel, null)
                .show();
    }

    private void saveEditedItem(InventoryItem item, EditText etName, EditText etQuantity, EditText etLocation) {
        String name = etName.getText().toString().trim();
        String quantity = etQuantity.getText().toString().trim();
        String location = etLocation.getText().toString().trim();

        if (name.isEmpty() || quantity.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, R.string.fill_all_item_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        boolean updated = inventoryDatabaseHelper.updateItem(
                item.getId(),
                selectedInventoryName,
                name,
                quantity,
                location
        );

        if (updated) {
            checkLowInventory(name, quantity);
            refreshInventory();
            Toast.makeText(this, R.string.item_updated, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.item_not_updated, Toast.LENGTH_SHORT).show();
        }
    }

    private void checkLowInventory(String name, String quantityText) {
        try {
            int qty = Integer.parseInt(quantityText);
            int threshold = 5;

            if (qty < threshold) {
                if (androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS)
                        == android.content.pm.PackageManager.PERMISSION_GRANTED) {

                    String phoneNumber = getSharedPreferences("AppPrefs", MODE_PRIVATE)
                            .getString("sms_number", "");

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

                        //Having problems with seeing emulator message. Try-catching to see if it is erroring out instead.
                        try {
                            smsManager.sendTextMessage(
                                    phoneNumber,
                                    null,
                                    "Low inventory alert: " + name + " is down to " + qty,
                                    null,
                                    null
                            );

                            Toast.makeText(this,
                                    "Low inventory SMS sent",
                                    Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            Toast.makeText(this,
                                    "SMS failed: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Error: Invalid quantity for inventory check.", Toast.LENGTH_LONG).show();
        }
    }
}
