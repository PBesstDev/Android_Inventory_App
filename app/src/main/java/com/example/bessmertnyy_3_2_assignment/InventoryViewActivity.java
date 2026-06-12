package com.example.bessmertnyy_3_2_assignment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

//Browse list of all available inventories
public class InventoryViewActivity extends AppCompatActivity {

    private InventoryDatabaseHelper inventoryDatabaseHelper;
    private ListView listViewInventories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventoryview);

        // Initialize database helper and ensure default data is available
        inventoryDatabaseHelper = new InventoryDatabaseHelper(this);
        inventoryDatabaseHelper.ensureExampleInventoryExists();

        // Bind UI components
        listViewInventories = findViewById(R.id.listViewInventories);
        
        // Initial data load
        loadInventoryNames();

        // Setup navigation to specific inventory details on click
        listViewInventories.setOnItemClickListener((parent, view, position, id) -> {
            String inventoryName = (String) parent.getItemAtPosition(position);
            Intent intent = new Intent(InventoryViewActivity.this, DataViewActivity.class);
            intent.putExtra("inventory_name", inventoryName);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the list when returning to this screen
        loadInventoryNames();
    }

    //returns all inventories from the database
    private void loadInventoryNames() {
        List<String> inventoryNames = inventoryDatabaseHelper.getInventoryNames();
        // Use a simple built-in layout for the list items
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, inventoryNames);
        listViewInventories.setAdapter(adapter);
    }
}