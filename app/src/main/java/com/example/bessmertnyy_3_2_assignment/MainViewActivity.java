package com.example.bessmertnyy_3_2_assignment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainview);

        Button btnUserProfile = findViewById(R.id.btn_user_profile);
        Button btnExistingInventories = findViewById(R.id.btn_existing_inventories);

        btnUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String activeUsername = getSharedPreferences("AppPrefs", MODE_PRIVATE)
                        .getString("logged_in_username", "");

                Intent intent = new Intent(MainViewActivity.this, UserProfileActivity.class);
                intent.putExtra("username", activeUsername);
                startActivity(intent);
            }
        });

        btnExistingInventories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainViewActivity.this, InventoryViewActivity.class);
                startActivity(intent);
            }
        });

    }
}
