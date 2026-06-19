package com.example.bessmertnyy_3_2_assignment;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class InventoryViewActivity extends AppCompatActivity {

    private InventoryDatabaseHelper myInventoryDatabase;
    private ListView listViewInventories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventoryview);

        myInventoryDatabase = new InventoryDatabaseHelper(this);
        myInventoryDatabase.ensureExampleInventoryExists();

        listViewInventories = findViewById(R.id.listViewInventories);
        FloatingActionButton fabCreateInventory = findViewById(R.id.fabCreateInventory);
        
        loadInventoryNames();

        listViewInventories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String inventoryName = (String) parent.getItemAtPosition(position);
                Intent intent = new Intent(InventoryViewActivity.this, DataViewActivity.class);
                intent.putExtra("inventory_name", inventoryName);
                startActivity(intent);
            }
        });

        fabCreateInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptCreateInventory();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadInventoryNames();
    }

    private void loadInventoryNames() {
        List<String> inventoryNames = myInventoryDatabase.getInventoryNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, inventoryNames);
        listViewInventories.setAdapter(adapter);
    }

    private void promptCreateInventory() {
        EditText etInventoryName = new EditText(this);
        etInventoryName.setHint(R.string.inventoryview_create_name_hint);

        new AlertDialog.Builder(this)
                .setTitle(R.string.inventoryview_create_title)
                .setView(etInventoryName)
                .setPositiveButton(R.string.inventoryview_btn_continue, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        continueCreateInventory(etInventoryName);
                    }
                })
                .setNegativeButton(R.string.additem_btn_cancel, null)
                .show();
    }

    private void continueCreateInventory(EditText etInventoryName) {
        String inventoryName = etInventoryName.getText().toString().trim();

        if (inventoryName.isEmpty()) {
            Toast.makeText(this, R.string.inventoryview_error_name_required, Toast.LENGTH_SHORT).show();
            return;
        }

        if (inventoryExists(inventoryName)) {
            Toast.makeText(this, R.string.inventoryview_error_exists, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(InventoryViewActivity.this, AddItemActivity.class);
        intent.putExtra("inventory_name", inventoryName);
        startActivity(intent);
        Toast.makeText(this, R.string.inventoryview_msg_add_first_item, Toast.LENGTH_SHORT).show();
    }

    private boolean inventoryExists(String inventoryName) {
        List<String> inventoryNames = myInventoryDatabase.getInventoryNames();
        for (String existingName : inventoryNames) {
            if (existingName.equalsIgnoreCase(inventoryName)) {
                return true;
            }
        }
        return false;
    }

}
