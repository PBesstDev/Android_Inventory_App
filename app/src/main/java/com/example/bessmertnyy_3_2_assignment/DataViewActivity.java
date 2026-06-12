package com.example.bessmertnyy_3_2_assignment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;


//View the contents of specific inventory in a grid/list format.
public class DataViewActivity extends AppCompatActivity {

    // UI components and adapter
    private RecyclerView recyclerView;
    private FloatingActionButton fabAddData;
    private InventoryAdapter adapter;
    private List<InventoryItem> inventoryList;
    
    // Database and selected context
    private InventoryDatabaseHelper inventoryDatabaseHelper;
    private String selectedInventoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dataview);

        // Setup database helper
        inventoryDatabaseHelper = new InventoryDatabaseHelper(this);
        inventoryDatabaseHelper.ensureExampleInventoryExists();

        // Retrieve the inventory name passed from InventoryViewActivity
        selectedInventoryName = getIntent().getStringExtra("inventory_name");
        if (selectedInventoryName == null || selectedInventoryName.trim().isEmpty()) {
            selectedInventoryName = InventoryDatabaseHelper.EXAMPLE_INVENTORY_NAME;
        }

        // 1. Bind UI elements to Java variables
        recyclerView = findViewById(R.id.recyclerViewData);
        fabAddData = findViewById(R.id.fabAddData);

        // 2. Setup the "Add Item" Floating Action Button
        fabAddData.setOnClickListener(v -> {
            Intent intent = new Intent(DataViewActivity.this, AddItemActivity.class);
            intent.putExtra("inventory_name", selectedInventoryName);
            startActivity(intent);
        });

        // 3. Setup the RecyclerView with a vertical layout
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        inventoryList = new ArrayList<>();
        
        // Define the delete action for the adapter
        adapter = new InventoryAdapter(inventoryList, item -> {
            boolean deleted = inventoryDatabaseHelper.deleteItem(item.getId());
            if (deleted) {
                refreshInventory();
                Toast.makeText(DataViewActivity.this, "Item deleted.", Toast.LENGTH_SHORT).show();
            }
        });
        
        recyclerView.setAdapter(adapter);
        
        // Populate the list with data
        refreshInventory();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data whenever the user returns to this screen
        refreshInventory();
    }

    //Clear current list and reload all items for selected database
    private void refreshInventory() {
        inventoryList.clear();
        inventoryList.addAll(inventoryDatabaseHelper.getAllItems(selectedInventoryName));
        adapter.notifyDataSetChanged();
    }
}